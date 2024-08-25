package moe.seikimo.droplet.world.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.block.BlockStorageV2;
import moe.seikimo.droplet.utils.objects.binary.BitArrayVersion;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@ToString
@RequiredArgsConstructor
public final class DropletChunkSection implements ChunkSection {
    public static final byte SECTION_VERSION = 9;
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

    private final List<BlockStorageV2> layers = new ArrayList<>();

    /**
     * Creates or defines a block storage "layer".
     *
     * @param layer The layer to create.
     */
    private BlockStorageV2 getLayer(int layer) {
        if (this.layers.size() <= layer) {
            for (var i = this.layers.size(); i <= layer; i++) {
                this.layers.add(new BlockStorageV2());
            }
        }

        return this.layers.get(layer);
    }

    @Override
    public boolean isEmpty() {
        return this.getLayers().stream()
                .allMatch(BlockStorageV2::isEmpty);
    }

    @Override
    public int getBlockAt(int x, int y, int z) {
        return this.getLayer(0).getState(x, y, z);
    }

    @Override
    public void setBlockAt(int x, int y, int z, int blockId) {
        this.getLayer(0).setState(x, y, z, blockId);
    }

    @Override
    public ByteBuf encodeBedrock() {
        var buffer = Unpooled.buffer();

        buffer.writeByte(SECTION_VERSION); // Version.
        buffer.writeByte(1); // Storage count.
        buffer.writeByte(this.getY() + 4); // Layer number.

        // Serialize layers.
        var blocks = this.getLayer(0);
        BlockStorageV2.serializeBedrock(blocks, buffer,
                BlockPalette::getBedrockRuntimeId);

        return buffer;
    }

    @Override
    public ByteBuf encodeJava() {
//        var bitArray = this.getStorage().getBitArray();
//        var palette = this.getStorage().getPalette();
//
//        var buffer = Unpooled.buffer();
//
//        // Get all non-air blocks in the chunk.
//        var nonAirBlocks = 0;
//        for (var y = 0; y < 16; y++) {
//            for (var z = 0; z < 16; z++) {
//                for (var x = 0; x < 16; x++) {
//                    var index = EncodingUtils.getIndex(x, y, z);
//                    var paletteIndex = bitArray.get(index);
//                    var blockState = palette.getInt(paletteIndex);
//                    if (blockState != BlockPalette.getJavaAirBlock())
//                        nonAirBlocks++;
//                }
//            }
//        }
//
//        buffer.writeShort(nonAirBlocks); // Non-air block count.
//
//        {
//            // Calculate the bits per entry for blocks.
//            // See: https://wiki.vg/Chunk_Format#Palette_formats
//            // This is a minimum value of 0.
//            // 0 = Single value
//            // 4-8 = Indirect
//            // 15-31** = Direct
//            var bitsPerEntry = Math.max(0, (int) Math.ceil(Math.log(palette.size()) / Math.log(2)));
//
//            buffer.writeByte(bitsPerEntry); // Bits per entry.
//            if (bitsPerEntry == 0) {
//                // Use a single value structure.
//                VarInts.writeInt(buffer, palette.getInt(0)); // The single value.
//                VarInts.writeInt(buffer, 0); // Data array length.
//            } else if (bitsPerEntry < 8) {
//                // Use an indirect palette structure.
//                // The registry ID is directly mapped to the block.
//
//                VarInts.writeInt(buffer, palette.size()); // Palette size.
//
//                // Translate the palette.
//                palette.forEach(entry -> {
//                    var blockState = BlockPalette.getPalette().get(entry);
//                    if (blockState != null) {
//                        VarInts.writeInt(buffer, blockState.getJavaRuntimeId());
//                    } else {
//                        Droplet.getLogger().warn("Block {} has no Droplet mapping.", entry);
//                    }
//                });
//
//                // Convert the block structure to YZX.
//                var newArray = new SimpleBitArray(bitsPerEntry, 4096);
//                for (var y = 0; y < 16; y++) {
//                    for (var z = 0; z < 16; z++) {
//                        for (var x = 0; x < 16; x++) {
//                            var index = EncodingUtils.getIndex(x, y, z);
//                            var converted = EncodingUtils.javaIndex(x, y, z);
//
//                            var blockId = bitArray.get(index);
//                            newArray.set(converted, blockId);
//                        }
//                    }
//                }
//
//                // Write the block data.
//                var words = newArray.getWordsLong();
//                VarInts.writeInt(buffer, words.length); // Data array length.
//                Arrays.stream(words).forEach(buffer::writeLong); // Data array.
//            }
//        }
//
//        {
//            // TODO: Encode biomes.
//            // https://wiki.vg/Chunk_Format#Example
//            buffer.writeByte(1); // bits per entry
//            buffer.writeByte(2); // palette length
//            buffer.writeBytes(new byte[]{0x27, 0x03});
//            buffer.writeByte(1); // long element length
//            buffer.writeBytes(new byte[]{
//                    (byte) 0xCC, (byte) 0xFF,
//                    (byte) 0xCC, (byte) 0xFF,
//                    (byte) 0xCC, (byte) 0xFF,
//                    (byte) 0xCC, (byte) 0xFF
//            });
//        }

        return Unpooled.buffer();
    }
}
