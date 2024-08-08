package moe.seikimo.droplet.entity;

import lombok.Getter;
import moe.seikimo.droplet.Droplet;
import moe.seikimo.droplet.utils.FileUtils;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.entity.*;

@Getter
public class DropletEntity implements Entity {
    private static final String IDENTIFIERS_PATH = "data/entity_identifiers.nbt";

    @Getter private static NbtMap entityIdentifiers;
    public static long nextEntityId = 1;

    /**
     * Loads entity identifiers.
     */
    public static void loadIdentifiers() {
        try {
            Droplet.getLogger().info("Loading entity definitions...");

            var stream = NbtUtils.createNetworkReader(
                    FileUtils.resource(IDENTIFIERS_PATH));
            DropletEntity.entityIdentifiers = (NbtMap) stream.readTag();

            stream.close();
            Droplet.getLogger().debug("Loaded {} entity identifiers.",
                    entityIdentifiers.getList("idlist", NbtType.COMPOUND).size());
        } catch (Exception exception) {
            Droplet.getLogger().warn("Failed to load entity definitions.", exception);
        }
    }

    private final long entityId;
    private final EntityData data = new EntityData();

    public DropletEntity(long entityId) {
        this.entityId = entityId;

        this.data.set(EntityDataTypes.HITBOX, NbtMap.builder()
                .putList("Hitboxes", NbtType.COMPOUND)
                .build());
        this.data.set(EntityDataTypes.SCALE, 1f);
        this.data.set(EntityDataTypes.LEASH_HOLDER, -1L);
        this.data.set(EntityDataTypes.OWNER_EID, -1L);
        this.data.set(EntityDataTypes.TARGET_EID, 0L);
        this.data.set(EntityDataTypes.NAME, "");
        this.data.set(EntityDataTypes.SCORE, "");
        this.data.set(EntityDataTypes.COLOR, (byte) 0);
    }
}
