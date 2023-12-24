package moe.seikimo.droplet.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import moe.seikimo.droplet.Droplet;
import moe.seikimo.droplet.utils.FileUtils;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleBlockDefinition;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;

import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public final class BlockPalette {
    private static final String PATH = "data/droplet_block_palette.nbt";

    @Getter private static final Int2ObjectMap<BlockState>
            palette = new Int2ObjectOpenHashMap<>();

    @Getter private static final Map<String, Integer> bedrockBlockMap = new HashMap<>();
    @Getter private static DefinitionRegistry<SimpleBlockDefinition> bedrockRegistry;

    /**
     * Loads the block palette from the resources.
     */
    public static void load() {
        try {
            Droplet.getLogger().info("Loading block palette...");

            var stream = new NBTInputStream(new DataInputStream(
                    new GZIPInputStream(FileUtils.resource(PATH))));
            var nbt = (NbtMap) stream.readTag();
            var palette = (NbtList<NbtMap>) nbt.getList(
                    "blocks", NbtType.COMPOUND);

            SimpleDefinitionRegistry.Builder<SimpleBlockDefinition> registry =
                    SimpleDefinitionRegistry.builder();

            // Parse the block palette into usable data.
            var definitions = new HashMap<Integer, BlockDefinition>();
            for (var id = 0; id < palette.size(); id++) {
                var blockData = palette.get(id);
                var bedrockId = blockData.getInt("bid");
                var javaState = blockData.getCompound("jstates");
                var bedrockState = blockData.getCompound("bstates");
                var javaIdentifier = blockData.getString("jname");
                var bedrockIdentifier = blockData.getString("bname");

                var blockState = new BlockState(bedrockId,
                        javaState, bedrockState,
                        javaIdentifier, bedrockIdentifier);

                // Add mappings for the Droplet ID.
                BlockPalette.palette.put(id, blockState);
                // Note the block identifier.
                BlockPalette.bedrockBlockMap.put(bedrockIdentifier, bedrockId);

                // Create a block definition for Bedrock.
                var definition = new SimpleBlockDefinition(
                        bedrockIdentifier, bedrockId, bedrockState);
                if (!definitions.containsKey(bedrockId)) {
                    definitions.put(bedrockId, definition);
                    registry.add(definition);
                }
            }

            // Create the registry.
            BlockPalette.bedrockRegistry = registry.build();

            stream.close();
            Droplet.getLogger().info("Loaded {} blocks into the block palette.", palette.size());
        } catch (Exception exception) {
            Droplet.getLogger().warn("Unable to read block palette. {}", exception.getMessage());
        }
    }

    /**
     * Fetches the Bedrock block definition from the Bedrock identifier.
     *
     * @param identifier The Bedrock identifier.
     * @param state The block state.
     * @return The block definition.
     */
    public static BlockDefinition getDefinition(String identifier, NbtMap state) {
        return BlockPalette.getBedrockBlockMap().entrySet().stream()
                .filter(entry -> entry.getKey().equals(identifier))
                .map(entry -> BlockPalette.getBedrockRegistry().getDefinition(entry.getValue()))
                .filter(Objects::nonNull)
                .filter(definition -> definition.getState().equals(state))
                .findFirst().orElse(null);
    }
}
