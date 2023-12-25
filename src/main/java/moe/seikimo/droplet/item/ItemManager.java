package moe.seikimo.droplet.item;

import com.google.gson.JsonArray;
import lombok.Getter;
import moe.seikimo.droplet.Droplet;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.utils.FileUtils;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;

import java.util.*;

@Getter
public final class ItemManager {
    private static final String ITEMS_PATH = "data/runtime_item_states.json";
    private static final String CREATIVE_PATH = "data/creative_items.json";

    /**
     * Loads the default item registry from the resources.
     */
    public static void load() {
        try {
            Droplet.getLogger().info("Loading items...");

            var definitionMap = new HashMap<String, ItemDefinition>();
            var creativeItems = new HashSet<ItemData>();

            {
                var data = EncodingUtils.jsonDecode(
                        FileUtils.resource(ITEMS_PATH), JsonArray.class);
                for (var element : data) {
                    var object = element.getAsJsonObject();
                    var id = object.get("id").getAsInt();
                    var name = object.get("name").getAsString();

                    var definition = new SimpleItemDefinition(name, id, false);
                    definitionMap.put(name, definition);
                }
            }

            Droplet.getLogger().info("Loaded {} items into the item registry.", definitionMap.size());

            {
                var data = EncodingUtils.jsonDecode(
                        FileUtils.resource(CREATIVE_PATH), JsonArray.class);
                for (var element : data) {
                    var object = element.getAsJsonObject();
                    var itemBuilder = ItemData.builder();

                    var name = object.get("name").getAsString();
                    itemBuilder.definition(definitionMap.get(name));

                    if (object.has("meta")) {
                        itemBuilder.damage(object.get("meta").getAsInt());
                    }

                    if (object.has("block_states")) {
                        var states = EncodingUtils.base64Decode(
                                object.get("block_states").getAsString());
                        var nbt = EncodingUtils.nbtDecode(states);

                        var blockDefinition = BlockPalette.getDefinition(name, nbt);
                        if (blockDefinition != null) itemBuilder.blockDefinition(blockDefinition);
                    }

                    if (object.has("nbt")) {
                        var nbtBytes = EncodingUtils.base64Decode(
                                object.get("nbt").getAsString());
                        itemBuilder.tag(EncodingUtils.nbtDecode(nbtBytes));
                    }

                    creativeItems.add(itemBuilder
                            .usingNetId(false).count(1).build());
                }
            }

            Server.getInstance().setItemManager(new ItemManager(definitionMap.values(), creativeItems));
            Droplet.getLogger().info("Loaded {} creative items.", creativeItems.size());
        } catch (Exception exception) {
            Droplet.getLogger().warn("Unable to read item states. {}", exception.getMessage());
        }
    }

    private final List<ItemDefinition> definitionsList = new ArrayList<>();
    private final Set<ItemData> creativeItems = new HashSet<>();
    private final DefinitionRegistry<ItemDefinition> registry;

    public ItemManager(
            Collection<ItemDefinition> definitions,
            Collection<ItemData> creativeItems
    ) {
        this.definitionsList.addAll(definitions);
        this.creativeItems.addAll(creativeItems);

        SimpleDefinitionRegistry.Builder<ItemDefinition> registry
                = SimpleDefinitionRegistry.builder();
        definitions.forEach(registry::add);
        this.registry = registry.build();
    }
}
