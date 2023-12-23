package moe.seikimo.droplet.data;

import java.io.File;

public interface Constants {
    File BEDROCK_BLOCK_PALETTE = new File(DataGenerator.getBaseDir(), "bedrock_block_palette.nbt");
    File JAVA_TO_BEDROCK_MAP = new File(DataGenerator.getBaseDir(), "blocksJ2B.json");
}
