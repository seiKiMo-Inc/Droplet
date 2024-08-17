package moe.seikimo.droplet.data.loaders;

import com.google.gson.JsonObject;
import lombok.Getter;
import moe.seikimo.droplet.data.Constants;
import moe.seikimo.droplet.data.Stream;
import moe.seikimo.droplet.block.MinecraftBlock;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The 'blocks.json' just so happens to also contain state ID mappings.
 * We use it for both mapping Bedrock to Java, but also to get the state ID.
 */
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
    private final Map<MinecraftBlock, Integer>
            blockStateIdMap = new HashMap<>();

    private MinecraftBlockMap() {
        this.readFromFile();
    }

    private void readFromFile() {
        logger.info("Reading Minecraft block state mappings.");

        var javaIdToBlock = new HashMap<Integer, MinecraftBlock>();

        try {
            var blockMap = Stream.json(Constants.MINECRAFT_BLOCK_MAP);

            for (var entry : blockMap.entrySet()) {
                var javaId = entry.getKey();
                var blockData = entry.getValue().getAsJsonObject();

                // Each blockData object has multiple states.
                var states = blockData.getAsJsonArray("states");
                for (var state : states) {
                    var stateData = state.getAsJsonObject();
                    var runtimeId = stateData.get("id").getAsInt();
                    var properties = stateData.has("properties") ?
                            stateData.getAsJsonObject("properties") : new JsonObject();

                    var block = MinecraftBlock.fromJson(javaId, properties);

                    this.getBlockStateIdMap().put(block, runtimeId);
                    javaIdToBlock.put(runtimeId, block);
                }
            }

            logger.info("Read {} Java block states from file.", this.getBlockStateIdMap().size());
        } catch (Exception ex) {
            logger.warn("Unable to read Minecraft block state mappings.", ex);
        }

        try (var mappings = Stream.nbt(Constants.BEDROCK_TO_JAVA_MAP)) {
            var map = ((NbtMap) mappings.readTag())
                    .getList("bedrock_mappings", NbtType.COMPOUND);

            var javaRuntimeId = -1;
            for (var entry : map) {
                javaRuntimeId++;

                var javaBlock = javaIdToBlock.get(javaRuntimeId);

                var bedrockName = STR."minecraft:\{entry.getString(
                        "bedrock_identifier", javaBlock.rawName())}";
                var bedrockState = entry.getCompound("state", NbtMap.EMPTY);

                var bedrockBlock = MinecraftBlock.fromNbt(bedrockName, bedrockState);

                this.getBedrockToJavaMap().put(bedrockBlock, javaBlock);
                this.getJavaToBedrockMap().put(javaBlock, bedrockBlock);
            }

            logger.info("Read {} mappings from file.", this.getJavaToBedrockMap().size());
        } catch (Exception ex) {
            logger.warn("Unable to read Java to Bedrock mappings.", ex);
        }

//        try (var data = Stream.nbt(Constants.BEDROCK_TO_JAVA_MAP) ) {
//            var map = ((NbtMap) data.readTag())
//                    .getList("bedrock_mappings", NbtType.COMPOUND);
//
//            var iterator = map.iterator();
//            while (iterator.hasNext()) {
//                var value = iterator.next();
//            }
//
//            var blockStateId = 0;
//            for (var entry : data.entrySet()) {
//                var javaTag = entry.getKey();
//                var bedrockData = entry.getValue().getAsJsonObject();
//
//                var bedrockName = bedrockData.get("bedrock_identifier").getAsString();
//                var bedrockState = bedrockData.has("bedrock_state") ?
//                        bedrockData.get("bedrock_state").getAsJsonObject() : new JsonObject();
//                var bedrockBlock = MinecraftBlock.fromJson(bedrockName, bedrockState);
//
//                var javaBlock = MinecraftBlock.fromString(javaTag);
//
//                this.getBedrockToJavaMap().put(bedrockBlock, javaBlock);
//                this.getJavaToBedrockMap().put(javaBlock, bedrockBlock);
//
//                this.getBlockStateIdMap().put(javaBlock, blockStateId++);
//            }
//
//            logger.info("Read {} mappings from file.", this.getJavaToBedrockMap().size());
//        } catch (Exception exception) {
//            logger.warn("Unable to read Minecraft block state mappings.", exception);
//        }
    }
}
