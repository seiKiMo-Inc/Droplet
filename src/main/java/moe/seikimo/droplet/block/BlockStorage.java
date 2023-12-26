package moe.seikimo.droplet.block;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import moe.seikimo.droplet.utils.objects.binary.BitArray;
import moe.seikimo.droplet.utils.objects.binary.BitArrayVersion;
import moe.seikimo.droplet.world.chunk.DropletChunkSection;

import java.util.Arrays;

@Getter
// Taken from GeyserMC's BlockStorage.java
public final class BlockStorage {
    public static final int SIZE = 4096;

    private final IntList palette;
    private BitArray bitArray;

    public BlockStorage(int airBlockId) {
        this(airBlockId, BitArrayVersion.V2);
    }

    public BlockStorage(int airBlockId, BitArrayVersion version) {
        this.bitArray = version.createArray(SIZE);
        this.palette = new IntArrayList(16);
        this.palette.add(airBlockId);
    }

    public BlockStorage(BitArray bitArray, IntList palette) {
        this.palette = palette;
        this.bitArray = bitArray;
    }

    /**
     * Fetches the index in the palette for the block of the runtime ID.
     * If the block is not in the palette, it will be added.
     *
     * @param blockRuntimeId The runtime ID of the block.
     * @return The index in the palette.
     */
    public int getIndexFor(int blockRuntimeId) {
        var index = this.palette.indexOf(blockRuntimeId);
        if (index != -1) return index;

        index = this.getPalette().size();
        this.getPalette().add(blockRuntimeId);

        var arrayVersion = this.getBitArray().getVersion();
        if (index > arrayVersion.getMaxEntryValue()) {
            var nextVersion = arrayVersion.next();
            if (nextVersion != null)
                this.resize(nextVersion);
        }

        return index;
    }

    /**
     * Resizes the existing bit array.
     *
     * @param version The new bit array version.
     */
    private void resize(BitArrayVersion version) {
        var newArray = version.createArray(SIZE);
        for (var i = 0; i < SIZE; i++) {
            newArray.set(i, this.getBitArray().get(i));
        }
        this.bitArray = newArray;
    }

    /**
     * @return An estimate of the size of the storage.
     */
    public int size() {
        var size = 1; // Palette header.
        size += this.getBitArray().getWordsInt().length * 4; // Words.
        size += 3; // Palette size.
        size += this.getPalette().size() * 3; // Palette.
        return size;
    }

    /**
     * @param index The index of the block.
     * @return The block at the index.
     */
    public int getBlock(int index) {
        return this.getPalette().getInt(this.getBitArray().get(index));
    }

    /**
     * @param index The index of the block.
     * @param blockRuntimeId The runtime ID of the block.
     */
    public void setBlock(int index, int blockRuntimeId) {
        this.getBitArray().set(index, this.getIndexFor(blockRuntimeId));
    }

    /**
     * Writes the palette data to the buffer.
     *
     * @param buffer The buffer to write to.
     */
    public void serialize(ByteBuf buffer) {
        var bitArray = this.getBitArray();
        var palette = this.getPalette();

        buffer.writeByte(DropletChunkSection.encodeHeader(bitArray.getVersion(), true));
        Arrays.stream(bitArray.getWordsInt()).forEach(buffer::writeIntLE);
        bitArray.writeSizeToNetwork(buffer, palette.size());
        palette.forEach(buffer::writeIntLE);
    }
}
