package moe.seikimo.droplet.data.loaders;

import lombok.Getter;
import moe.seikimo.droplet.data.Constants;
import moe.seikimo.droplet.data.Stream;
import moe.seikimo.droplet.block.MinecraftBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class JavaBedrockBlockMap {
    @Getter private static final JavaBedrockBlockMap instance;
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Java Bedrock Block Map");

    static {
        instance = new JavaBedrockBlockMap();
    }

    private final Map<MinecraftBlock, MinecraftBlock>
            javaToBedrockMap = new HashMap<>(),
            bedrockToJavaMap = new HashMap<>();

    private JavaBedrockBlockMap() {
        this.readFromFile();
    }

    private void readFromFile() {
        logger.info("Reading Java -> Bedrock block mappings from file.");

        try {
            var data = Stream.json(Constants.JAVA_TO_BEDROCK_MAP);

            for (var entry : data.entrySet()) {
                var javaTag = entry.getKey();
                var bedrockTag = entry.getValue().getAsString();

                var javaBlock = MinecraftBlock.fromString(javaTag);
                var bedrockBlock = MinecraftBlock.fromString(bedrockTag);

                this.javaToBedrockMap.put(javaBlock, bedrockBlock);
                this.bedrockToJavaMap.put(bedrockBlock, javaBlock);
            }

            logger.info("Read {} mappings from file.", this.getJavaToBedrockMap().size());
        } catch (Exception exception) {
            logger.warn("Unable to read Java -> Bedrock block mappings.", exception);
        }
    }
}
