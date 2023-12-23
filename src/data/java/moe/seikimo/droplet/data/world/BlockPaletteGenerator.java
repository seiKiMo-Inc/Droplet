package moe.seikimo.droplet.data.world;

import lombok.Getter;
import moe.seikimo.droplet.data.Constants;
import moe.seikimo.droplet.data.Stream;
import moe.seikimo.droplet.data.loaders.BedrockBlockPalette;
import moe.seikimo.droplet.data.loaders.JavaBedrockBlockMap;
import moe.seikimo.droplet.data.loaders.MinecraftBlockMap;
import moe.seikimo.droplet.data.types.MinecraftBlock;
import org.cloudburstmc.nbt.NbtMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public final class BlockPaletteGenerator {
    private static final boolean USE_BLOCKS = false;

    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Block Palette Generator");

    /**
     * Generates the Droplet block palette.
     */
    public static void generate() {
        var palette = BedrockBlockPalette.getInstance().getBlocks();
        var blocks = MinecraftBlockMap.getInstance().getJavaToBedrockMap();
        var mapping = JavaBedrockBlockMap.getInstance().getJavaToBedrockMap();

        // Create a map for the palette.
        var paletteMap = new HashMap<Integer, Integer>();
        palette.forEach((id, block) -> {
            var minecraft = MinecraftBlock.fromBedrock(block);
            paletteMap.put(minecraft.hashCode(), id);
        });

        var paletteIndex = 0;
        var dropletPalette = NbtMap.builder();

        var noIdBlocks = new ArrayList<MinecraftBlock>();
        for (var entry : USE_BLOCKS ?
                blocks.entrySet() : mapping.entrySet()) {
            var block = NbtMap.builder();
            var javaBlock = entry.getKey();
            var bedrockBlock = entry.getValue();

            var bedrockId = paletteMap.get(bedrockBlock.hashCode());
            var javaBedrockId = paletteMap.get(javaBlock.hashCode());
            if (bedrockId != null) {
                block.putInt("bid", bedrockId);
            } else if (javaBedrockId != null) {
                block.putInt("bid", javaBedrockId);
            } else {
                noIdBlocks.add(javaBlock);
            }

            block
                    .putString("jname", javaBlock.name())
                    .putString("bname", bedrockBlock.name())
                    .putCompound("jstates", javaBlock.propertiesNbt())
                    .putCompound("bstates", bedrockBlock.propertiesNbt());

            dropletPalette.putCompound(
                    String.valueOf(paletteIndex++),
                    block.build());
        }

        try {
            // Write to file.
            var stream = Stream.oNbt(Constants.DROPLET_PALETTE_OUTPUT);
            stream.writeTag(dropletPalette.build());
            stream.close();

            BlockPaletteGenerator.getLogger().info("Wrote block palette to file.");

            // Write blocks with no ID.
            var noIdBuilder = new StringBuilder();
            noIdBuilder.append("Blocks with no ID:\n");

            noIdBlocks.forEach(block ->
                    noIdBuilder.append(String.format("(%s): %s\n",
                            block.hashCode(), block)));

            Files.writeString(Constants.NO_ID_DEBUG_OUTPUT.toPath(), noIdBuilder.toString());
            BlockPaletteGenerator.getLogger().warn("Blocks with no ID: {}", noIdBlocks.size());
        } catch (Exception exception) {
            BlockPaletteGenerator.getLogger().warn("Unable to write block palette.", exception);
        }
    }
}
