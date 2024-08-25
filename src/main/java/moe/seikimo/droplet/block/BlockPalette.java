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

    /** This is a map of Droplet block IDs -> Minecraft block states. */
    @Getter private static final Int2ObjectMap<BlockState>
            palette = new Int2ObjectOpenHashMap<>();

    /** This is used for reading Anvil-formatted chunks. */
    @Getter private static final Map<MinecraftBlock, Integer>
            javaPaletteMap = new HashMap<>();

    @Getter private static int
            airBlock = 0,
            javaAirBlock = 0,
            bedrockAirBlock = 0;

    @Getter private static final Map<String, Integer>
            bedrockBlockMap = new HashMap<>();
    @Getter private static DefinitionRegistry<BlockDefinition> bedrockRegistry;

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

            SimpleDefinitionRegistry.Builder<BlockDefinition> registry =
                    SimpleDefinitionRegistry.builder();

            // Parse the block palette into usable data.
            var definitions = new HashMap<Integer, BlockDefinition>();
            for (var id = 0; id < palette.size(); id++) {
                var blockData = palette.get(id);
                var javaId = blockData.getInt("jid");
                var bedrockId = blockData.getInt("bid");
                var javaState = blockData.getCompound("jstates");
                var bedrockState = blockData.getCompound("bstates");
                var javaIdentifier = blockData.getString("jname");
                var bedrockIdentifier = blockData.getString("bname");

                // Create data holders for the block.
                var blockState = new BlockState(
                        javaId, bedrockId,
                        javaState, bedrockState,
                        javaIdentifier, bedrockIdentifier);
                var minecraftBlock = MinecraftBlock.fromNbt(
                        javaIdentifier, javaState);

                // Add mappings for the Droplet ID.
                BlockPalette.palette.put(id, blockState);
                BlockPalette.javaPaletteMap.put(minecraftBlock, id);
                // Note the block identifier.
                BlockPalette.bedrockBlockMap.put(bedrockIdentifier, bedrockId);

                // Set the air block.
                if (javaIdentifier.equals("minecraft:air")) {
                    BlockPalette.airBlock = id;
                    BlockPalette.javaAirBlock = javaId;
                    BlockPalette.bedrockAirBlock = bedrockId;
                }

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
            Droplet.getLogger().debug("Loaded {} blocks into the block palette.", palette.size());
        } catch (Exception exception) {
            Droplet.getLogger().warn("Unable to read block palette. {}", exception.getMessage());
        }
    }

    /**
     * Fetches the runtime ID of a block state from the Droplet runtime ID.
     *
     * @param dropletId The Droplet runtime ID.
     * @return The Java runtime ID.
     */
    public static int getBedrockRuntimeId(int dropletId) {
        return BlockPalette.palette.get(dropletId).getBedrockRuntimeId();
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
                .filter(definition -> ((SimpleBlockDefinition) definition).getState().equals(state))
                .findFirst().orElse(null);
    }
}
