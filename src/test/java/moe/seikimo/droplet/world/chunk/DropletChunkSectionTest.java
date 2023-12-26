package moe.seikimo.droplet.world.chunk;

import io.netty.buffer.ByteBuf;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.utils.EncodingUtils;
import org.cloudburstmc.protocol.common.util.VarInts;
import org.junit.Assert;
import org.junit.Test;

import static moe.seikimo.droplet.utils.EncodingUtils.toBytes;

public final class DropletChunkSectionTest {
    /** This is the 1.20.50 block ID for a 'cobblestone_wall'. */
    private static final int BLOCK_ID_1 = 18660;
    private static final int JAVA_ID_1 = 8009;
    /** This is the 1.20.50 block ID for a 'cobbled_deepslate_wall'. */
    private static final int BLOCK_ID_2 = 18701;
    private static final int JAVA_ID_2 = 25063;
    /** This is the 1.20.50 block ID for a 'red_sandstone_wall'. */
    private static final int BLOCK_ID_3 = 18849;
    private static final int JAVA_ID_3 = 15121;

    /**
     * The first two bytes should be the number of non-air blocks
     * in the chunk section.
     */
    private static final int BLOCKS_IN_SECTION = 4096;
    private static final boolean CHECK_BLOCK_COUNT = false;

    private static final String INVALID_STATE = "Expected block ID %s at (%s, %s, %s) but found %s.";

    /**
     * Checks for proper encoding of a chunk section into the Java chunk format.
     */
    @Test public void encodeJavaChunk() {
        // Load palette data.
        BlockPalette.load();

        var section = new DropletChunkSection(0);

        // Fill the section with cobblestone walls.
        for (var y = 0; y < 16; y++) {
            var blockId = switch (y % 3) {
                case 0 -> BLOCK_ID_1;
                case 1 -> BLOCK_ID_2;
                case 2 -> BLOCK_ID_3;
                default -> throw new IllegalStateException("Unexpected value: " + y % 3);
            };

            for (var z = 0; z < 16; z++) {
                for (var x = 0; x < 16; x++) {
                    section.setBlockAt(x, y, z, blockId);
                }
            }
        }

        // Encode the section.
        var encodedData = section.encodeJava();
        System.out.println("Encoded data length: " + encodedData.readableBytes());
        System.out.println(">>> [Encoded Data] " + firstBytes(encodedData, 10));

        // Compare the encoded data to the expected data.
        if (CHECK_BLOCK_COUNT) {
            var blockCount = toBytes(encodedData.readBytes(2));
            Assert.assertEquals(blockCount, new byte[] { 0x0, 0x16 });
        }

        var bitsPerEntry = encodedData.readByte();
        System.out.println("Bits per entry: " + bitsPerEntry);
        if (bitsPerEntry < 4) {
            Assert.fail("Valid data does not have less than 4 bits per entry. Found " + bitsPerEntry + ".");
        } else if (bitsPerEntry < 8) {
            System.out.println("Detected indirect palette container.");

            // Read the palette.
            var paletteLength = VarInts.readInt(encodedData);

            // The palette size is air + other blocks.
            Assert.assertEquals(3 + 1, paletteLength);

            var palette = new int[paletteLength];
            for (var id = 0; id < paletteLength; id++) {
                var stateId = VarInts.readInt(encodedData);
                palette[id] = stateId;
                System.out.println("Found state ID " + stateId + " at palette index " + id + ".");
            }

            // Read the blocks.
            var dataArrayLength = VarInts.readInt(encodedData);

            // 256 = (16 * 16 * 16) * bitsPerEntry / 64
            Assert.assertEquals(256, dataArrayLength);

            var dataArray = new long[dataArrayLength];
            for (var i = 0; i < dataArrayLength; i++) {
                dataArray[i] = encodedData.readLong();
            }

            var valueMask = (1 << bitsPerEntry) - 1;

            for (var y = 0; y < 16; y++) {
                for (var z = 0; z < 16; z++) {
                    for (var x = 0; x < 16; x++) {
                        var index = EncodingUtils.anvilIndex(x, y, z);
                        var startLong = (index * bitsPerEntry) / 64;
                        var startOffset = (index * bitsPerEntry) % 64;
                        var endLong = ((index + 1) * bitsPerEntry - 1) / 64;

                        var data = startLong == endLong ?
                                dataArray[startLong] >> startOffset :
                                (dataArray[startLong] >> startOffset) | (dataArray[endLong] << (64 - startOffset));
                        var paletteIndex = (int) (data & valueMask);
                        var blockId = palette[paletteIndex];

                        var expectedId = switch (y % 3) {
                            case 0 -> JAVA_ID_1;
                            case 1 -> JAVA_ID_2;
                            case 2 -> JAVA_ID_3;
                            default -> throw new IllegalStateException("Unexpected value: " + y % 3);
                        };

                        Assert.assertEquals(INVALID_STATE.formatted(
                                expectedId, x, y, z, blockId
                        ), expectedId, blockId);
                    }
                }
            }
        } else {
            System.out.println("Detected direct palette container.");
        }
    }

    /**
     * Converts the first specified bytes into a string.
     *
     * @param bytes The bytes to convert.
     * @param count The number of bytes to convert.
     * @return The string.
     */
    private static String firstBytes(ByteBuf bytes, int count) {
        var copy = bytes.copy();

        var builder = new StringBuilder();
        for (var i = 0; i < count; i++) {
            builder.append(copy.readByte()).append(" ");
        }
        return builder.toString();
    }
}
