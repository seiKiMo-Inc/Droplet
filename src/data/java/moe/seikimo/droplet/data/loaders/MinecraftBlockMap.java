package moe.seikimo.droplet.data.loaders;

import com.google.gson.JsonObject;
import lombok.Getter;
import moe.seikimo.droplet.data.Constants;
import moe.seikimo.droplet.data.Stream;
import moe.seikimo.droplet.block.MinecraftBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class MinecraftBlockMap {
    @Getter private static final MinecraftBlockMap instance;
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Minecraft Block Map");

    static {
        instance = new MinecraftBlockMap();
    }

    private final Map<MinecraftBlock, MinecraftBlock>
            javaToBedrockMap = new HashMap<>(),
            bedrockToJavaMap = new HashMap<>();

    private MinecraftBlockMap() {
        this.readFromFile();
    }

    private void readFromFile() {
        logger.info("Reading Minecraft block state mappings.");

        try {
            var data = Stream.json(Constants.MINECRAFT_BLOCK_MAP);

            for (var entry : data.entrySet()) {
                var javaTag = entry.getKey();
                var bedrockData = entry.getValue().getAsJsonObject();

                var bedrockName = bedrockData.get("bedrock_identifier").getAsString();
                var bedrockState = bedrockData.has("bedrock_state") ?
                        bedrockData.get("bedrock_state").getAsJsonObject() : new JsonObject();
                var bedrockBlock = MinecraftBlock.fromJson(bedrockName, bedrockState);

                var javaBlock = MinecraftBlock.fromString(javaTag);
                this.getBedrockToJavaMap().put(bedrockBlock, javaBlock);
                this.getJavaToBedrockMap().put(javaBlock, bedrockBlock);
            }

            logger.info("Read {} mappings from file.", this.getJavaToBedrockMap().size());
        } catch (Exception exception) {
            logger.warn("Unable to read Minecraft block state mappings.", exception);
        }
    }
}
