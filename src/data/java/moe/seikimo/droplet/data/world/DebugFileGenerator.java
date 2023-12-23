package moe.seikimo.droplet.data.world;

import lombok.Getter;
import moe.seikimo.droplet.data.Constants;
import moe.seikimo.droplet.data.loaders.BedrockBlockPalette;
import moe.seikimo.droplet.data.loaders.JavaBedrockBlockMap;
import moe.seikimo.droplet.data.types.MinecraftBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;

public final class DebugFileGenerator {
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Debug File Generator");

    /**
     * Generates a debug file.
     */
    public static void generate() {
        var palette = BedrockBlockPalette.getInstance().getBlocks();
        var mapping = JavaBedrockBlockMap.getInstance().getJavaToBedrockMap();

        var paletteBuilder = new StringBuilder();
        paletteBuilder.append("Bedrock Block Palette:\n");

        palette.forEach((id, bedrock) -> {
            var minecraft = MinecraftBlock.fromBedrock(bedrock);
            paletteBuilder.append(String.format("(%s): %s -> %s [%s]\n",
                    id, bedrock, minecraft, minecraft.hashCode()));
        });

        var mappingBuilder = new StringBuilder();
        mappingBuilder.append("Java -> Bedrock Block Map:\n");

        mapping.forEach((java, bedrock) ->
                mappingBuilder.append(String.format("%s -> %s [%s, %s]\n",
                        java, bedrock, java.hashCode(), bedrock.hashCode())));

        var blockBuilder = new StringBuilder();
        blockBuilder.append("Java Block Map:\n");

        JavaBedrockBlockMap.getInstance().getJavaToBedrockMap().forEach((java, bedrock) ->
                blockBuilder.append(String.format("%s -> %s [%s, %s]\n",
                        java, bedrock, java.hashCode(), bedrock.hashCode())));

        try {
            Files.writeString(Constants.PALETTE_DEBUG_OUTPUT.toPath(), paletteBuilder.toString());
            Files.writeString(Constants.MAPPING_DEBUG_OUTPUT.toPath(), mappingBuilder.toString());
            Files.writeString(Constants.BLOCK_DEBUG_OUTPUT.toPath(), blockBuilder.toString());
        } catch (Exception exception) {
            logger.warn("Unable to write debug file.", exception);
        }
    }
}
