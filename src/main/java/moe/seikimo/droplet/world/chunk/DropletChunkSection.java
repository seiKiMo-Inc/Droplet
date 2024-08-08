package moe.seikimo.droplet.world.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import moe.seikimo.droplet.Droplet;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.block.BlockStorage;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.utils.objects.binary.BitArray;
import moe.seikimo.droplet.utils.objects.binary.BitArrayVersion;
import moe.seikimo.droplet.utils.objects.binary.SimpleBitArray;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.util.Arrays;
import java.util.Collections;

@Getter
@ToString
@RequiredArgsConstructor
public final class DropletChunkSection implements ChunkSection {
    private static final byte SECTION_VERSION = 9;
    private static final int PALETTE_SIZE = 4096;

    /**
     * Encodes the palette header.
     *
     * @param version The bit array version.
     * @param isRuntime Whether this is a runtime palette.
     * @return The palette header.
     */
    public static int encodeHeader(BitArrayVersion version, boolean isRuntime) {
        return (version.getId() << 1) | (isRuntime ? 1 : 0);
    }

    private final int y;

    private final BlockStorage storage = new BlockStorage(BlockPalette.getAirBlock());

    @Override
    public int getBlockAt(int x, int y, int z) {
        return this.getStorage().getBlock(
                EncodingUtils.getIndex(x, y, z));
    }

    @Override
    public void setBlockAt(int x, int y, int z, int blockId) {
        this.getStorage().setBlock(
                EncodingUtils.getIndex(x, y, z), blockId);
    }

    @Override
    public ByteBuf encodeBedrock() {
        var buffer = Unpooled.buffer();

        buffer.writeByte(SECTION_VERSION); // Version.
        buffer.writeByte(1); // Block storage count.
        buffer.writeByte(this.getY()); // Layer number.

        {
            var palette = this.getStorage().getPalette();

            var bedrockPalette = new IntArrayList();
            bedrockPalette.addAll(Collections.nCopies(
                    palette.size(), 0));

            var bitArray = BitArrayVersion.V2.createArray(PALETTE_SIZE);

            // Convert the palette from Droplet to Bedrock.
            var blockPalette = BlockPalette.getPalette();
            for (int dropletId : palette) {
                var paletteIndex = palette.indexOf(dropletId);
                var blockState = blockPalette.get(dropletId);
                if (blockState != null) {
                    bedrockPalette.set(paletteIndex, blockState.getBedrockRuntimeId());
                    bitArray = this.checkResize(paletteIndex, bitArray);
                } else {
                    Droplet.getLogger().warn("Block {} has no Droplet mapping.", dropletId);
                }
            }

            // Remap the block states to our appropriately sized bit array.
            var storageBits = this.getStorage().getBitArray();
            for (var i = 0; i < storageBits.size(); i++) {
                var dropletId = storageBits.get(i);
                var paletteIndex = palette.indexOf(dropletId);
                bitArray.set(i, paletteIndex);
            }

            buffer.writeByte(DropletChunkSection.encodeHeader(
                    bitArray.getVersion(), true)); // Palette header.
            Arrays.stream(bitArray.getWordsInt()).forEach(buffer::writeIntLE); // Words.
            bitArray.writeSizeToNetwork(buffer, bedrockPalette.size()); // Palette size.
            bedrockPalette.forEach(entry -> VarInts.writeInt(buffer, entry)); // Palette.
        }

        return buffer;
    }

    @Override
    public ByteBuf encodeJava() {
        var bitArray = this.getStorage().getBitArray();
        var palette = this.getStorage().getPalette();

        var buffer = Unpooled.buffer();

        // Get all non-air blocks in the chunk.
        var nonAirBlocks = 0;
        for (var y = 0; y < 16; y++) {
            for (var z = 0; z < 16; z++) {
                for (var x = 0; x < 16; x++) {
                    var index = EncodingUtils.getIndex(x, y, z);
                    var paletteIndex = bitArray.get(index);
                    var blockState = palette.getInt(paletteIndex);
                    if (blockState != BlockPalette.getAirBlock())
                        nonAirBlocks++;
                }
            }
        }

        buffer.writeShort(nonAirBlocks); // Non-air block count.

        {
            // Calculate the bits per entry for blocks.
            // This is a minimum value of 4.
            var bitsPerEntry = Math.max(4, (int) Math.ceil(Math.log(palette.size()) / Math.log(2)));

            buffer.writeByte(bitsPerEntry); // Bits per entry.
            if (bitsPerEntry < 8) {
                // Use an indirect palette structure.
                // The registry ID is directly mapped to the block.

                VarInts.writeInt(buffer, palette.size()); // Palette size.

                // Translate the palette.
                palette.forEach(entry -> {
                    var blockState = BlockPalette.getPalette().get(entry);
                    if (blockState != null) {
                        VarInts.writeInt(buffer, blockState.getJavaRuntimeId());
                    } else {
                        Droplet.getLogger().warn("Block {} has no Droplet mapping.", entry);
                    }
                });
            }

            // Convert the block structure to YZX.
            var newArray = new SimpleBitArray(bitsPerEntry, 4096);
            for (var y = 0; y < 16; y++) {
                for (var z = 0; z < 16; z++) {
                    for (var x = 0; x < 16; x++) {
                        var index = EncodingUtils.getIndex(x, y, z);
                        var converted = EncodingUtils.javaIndex(x, y, z);

                        var blockId = bitArray.get(index);
                        newArray.set(converted, blockId);
                    }
                }
            }

            // Write the block data.
            var words = newArray.getWordsLong();
            VarInts.writeInt(buffer, words.length); // Data array length.
            Arrays.stream(words).forEach(buffer::writeLong); // Data array.
        }

        {
            // TODO: Encode biomes.
            buffer.writeByte(0); // Bits per entry.
            // buffer.writeByte(0); // Palette length.
            buffer.writeByte(0); // Palette entry.
            buffer.writeByte(0); // Data array length.
        }

        return buffer;
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
