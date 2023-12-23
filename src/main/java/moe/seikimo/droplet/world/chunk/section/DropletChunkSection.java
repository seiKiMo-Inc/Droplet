package moe.seikimo.droplet.world.chunk.section;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.block.Block;
import moe.seikimo.droplet.block.DropletBlock;
import moe.seikimo.droplet.utils.objects.binary.BitArray;
import moe.seikimo.droplet.utils.objects.binary.BitArrayVersion;
import moe.seikimo.droplet.world.chunk.DropletChunk;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public final class DropletChunkSection implements ChunkSection {
    private static final byte SECTION_VERSION = 9;
    private static final int PALETTE_SIZE = 4096;

    /**
     * Encodes the palette header.
     *
     * @param version The bit array version.
     * @param isRuntime Whether or not this is a runtime palette.
     * @return The palette header.
     */
    public static int encodeHeader(BitArrayVersion version, boolean isRuntime) {
        return (version.getId() << 1) | (isRuntime ? 1 : 0);
    }

    private final int y;

    private final Int2IntMap
            palette = new Int2IntArrayMap(),
            blockStates = new Int2IntArrayMap();

    @Override
    public Block getBlockAt(int x, int y, int z) {
        var index = EncodingUtils.getIndex(x, y, z);
        var state = this.getBlockStates().get(index);

        return DropletBlock.fromState(state);
    }

    @Override
    public void setBlockAt(int x, int y, int z, int paletteIndex) {
        var index = EncodingUtils.getIndex(x, y, z);
        this.getBlockStates().put(index, paletteIndex);
    }

    @Override
    public ByteBuf encodeBedrock() {
        var buffer = Unpooled.buffer();

        buffer.writeByte(this.getY()); // Layer number.

        {
            var bedrockPalette = new IntArrayList();
            var bitArray = BitArrayVersion.V2.createArray(PALETTE_SIZE);

            // Convert the palette from Droplet to Bedrock.
            var blockPalette = BlockPalette.getPalette();
            this.getPalette().forEach((paletteIndex, dropletId) -> {
                var blockState = blockPalette.get((int) dropletId);
                if (blockState != null) {
                    bedrockPalette.set((int) paletteIndex, blockState.getBedrockRuntimeId());
                    this.checkResize(paletteIndex, bitArray);
                }
            });

            // Encode the chunk data into the Bedrock format.
            this.getBlockStates().forEach(bitArray::set);

            buffer.writeByte(DropletChunkSection.encodeHeader(
                    bitArray.getVersion(), true)); // Palette header.
            Arrays.stream(bitArray.getWords()).forEach(buffer::writeIntLE); // Words.
            bitArray.writeSizeToNetwork(buffer, bedrockPalette.size()); // Palette size.
            bedrockPalette.forEach(buffer::writeIntLE); // Palette.
        }

        return buffer;
    }

    @Override
    public byte[] encodeJava() {
        return new byte[0];
    }

    /**
     * Automatically resizes a bit array if the index is too large.
     *
     * @param index The index to check.
     * @param array The bit array to check.
     * @return The bit array.
     */
    private BitArray checkResize(int index, BitArray array) {
        var version = array.getVersion();
        if (index > version.getMaxEntryValue()) {
            var nextVersion = version.next();
            if (nextVersion == null) return array;

            var newArray = nextVersion.createArray(PALETTE_SIZE);
            for (var i = 0; i < PALETTE_SIZE; i++) {
                newArray.set(i, array.get(i));
            }
            return newArray;
        }

        return array;
    }
}
