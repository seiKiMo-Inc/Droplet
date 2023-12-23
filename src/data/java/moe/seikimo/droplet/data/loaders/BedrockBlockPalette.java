package moe.seikimo.droplet.data.loaders;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import moe.seikimo.droplet.data.Constants;
import moe.seikimo.droplet.data.Stream;
import moe.seikimo.droplet.data.types.BedrockBlock;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BedrockBlockPalette {
    @Getter private static final BedrockBlockPalette instance;
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Bedrock Block Palette");

    static {
        instance = new BedrockBlockPalette();
    }

    @Getter private final Int2ObjectMap<BedrockBlock> blocks
            = new Int2ObjectOpenHashMap<>();

    private BedrockBlockPalette() {
        this.readFromFile();
    }

    private void readFromFile() {
        logger.info("Reading block palette from file.");

        try {
            var paletteStream = Stream.nbt(Constants.BEDROCK_BLOCK_PALETTE);
            var palette = paletteStream.readValue(NbtType.COMPOUND);

            for (var entry : palette.entrySet()) {
                var runtimeId = Integer.parseInt(entry.getKey());
                var block = BedrockBlock.fromNbt((NbtMap) entry.getValue());
                this.getBlocks().put(runtimeId, block);
            }

            paletteStream.close();
            logger.info("Read {} blocks from file.", this.getBlocks().size());
        } catch (Exception exception) {
            logger.warn("Unable to read block palette.", exception);
        }
    }
}
