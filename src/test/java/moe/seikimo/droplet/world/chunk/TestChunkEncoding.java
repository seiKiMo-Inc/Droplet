package moe.seikimo.droplet.world.chunk;

import lombok.extern.slf4j.Slf4j;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.utils.Preconditions;
import moe.seikimo.droplet.world.World;
import moe.seikimo.droplet.world.io.AnvilFormatReader;
import org.cloudburstmc.protocol.common.util.VarInts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * Tests chunk encoding for Java and Bedrock based on world files.
 */
@Slf4j
public final class TestChunkEncoding {
    private static World world;
    private static ChunkSection section;

    @BeforeAll
    public static void beforeAll() throws Exception {
        BlockPalette.load();

        var worldDir = new File("worlds/world");
        Preconditions.fileExists(worldDir);
        Preconditions.canReadFile(worldDir);

        // Read the test world.
        var reader = new AnvilFormatReader();
        TestChunkEncoding.world = reader.read(worldDir);

        // Get the first chunk section.
        var chunk = world.getChunkAt(0, 0); // Fetches the (0, 0) chunk in the (0, 0) region.
        TestChunkEncoding.section = chunk.getSectionByIndex(-4); // Fetches the first chunk section (y = -64 to -48).
    }

    @Test
    public void encodeBedrockChunkSection() {
        var encoded = section.encodeBedrock();

        // Check the encoded data version.
        Assertions.assertEquals(encoded.readByte(), DropletChunkSection.SECTION_VERSION);

        var storages = encoded.readByte();
        log.info("Block storage count: {}", storages);

        var sectionY = encoded.readByte();
        Assertions.assertEquals(sectionY, section.getY());
        log.info("Section Y level: {}", sectionY);

        // Read each block storage.
        for (var i = 0; i < storages; i++) {
            log.info("---------- STORAGE {} ----------", i);

            var flags = encoded.readByte();
            var bitsPerBlock = flags >> 1;
            var isRuntime = (flags & 1) == 1;
            log.info("Bits per block: {}; runtime? {}", bitsPerBlock, isRuntime);

            var blocksPerWord = (int) Math.floor(32f / bitsPerBlock);
            var wordsPerChunk = (int) Math.ceil(4096f / blocksPerWord);

            // Read expected word count.
            // TODO: Validate words.
            encoded.readBytes(wordsPerChunk * 4);

            // Validate palette.
            var paletteLength = VarInts.readInt(encoded);
            log.info("Palette length: {}", paletteLength);

            for (var pIndex = 0; pIndex < paletteLength; pIndex++) {
                var blockState = BlockPalette
                        .getBedrockRegistry()
                        .getDefinition(VarInts.readInt(encoded));
                log.info("Palette index: {}, state: {}", pIndex, blockState);
            }
        }
    }
}
