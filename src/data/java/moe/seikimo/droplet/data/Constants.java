package moe.seikimo.droplet.data;

import java.io.File;

public interface Constants {
    File BEDROCK_BLOCK_PALETTE = new File(DataGenerator.getBaseDir(), "bedrock_block_palette.nbt");
    File JAVA_TO_BEDROCK_MAP = new File(DataGenerator.getBaseDir(), "blocksJ2B.json");
    File BEDROCK_TO_JAVA_MAP = new File(DataGenerator.getBaseDir(), "blocks.nbt");
    File MINECRAFT_BLOCK_MAP = new File(DataGenerator.getBaseDir(), "blocks.json");

    File DROPLET_PALETTE_OUTPUT = new File(DataGenerator.getBaseDir(), "output/droplet_block_palette.nbt");
    File PALETTE_DEBUG_OUTPUT = new File(DataGenerator.getBaseDir(), "output/palette_debug.txt");
    File MAPPING_DEBUG_OUTPUT = new File(DataGenerator.getBaseDir(), "output/mapping_debug.txt");
    File BLOCK_DEBUG_OUTPUT = new File(DataGenerator.getBaseDir(), "output/block_debug.txt");
    File NO_ID_DEBUG_OUTPUT = new File(DataGenerator.getBaseDir(), "output/no_id.txt");
}
