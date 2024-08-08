package moe.seikimo.droplet.entity;

import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataMap;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataType;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityProperties;

import java.util.EnumSet;

public final class EntityData {
    private final EntityDataMap data = new EntityDataMap();
    private final EnumSet<EntityFlag> flags = EnumSet.noneOf(EntityFlag.class);

    public EntityData() {
        this.flags.add(EntityFlag.ALWAYS_SHOW_NAME);
        this.flags.add(EntityFlag.HAS_GRAVITY);
        this.flags.add(EntityFlag.CAN_SHOW_NAME);
        this.flags.add(EntityFlag.HAS_COLLISION);
    }

    /**
     * Puts all data from the internal map to the given map.
     *
     * @param data The map to put the data into.
     * @param properties The map to put flags into.
     */
    public void restore(EntityDataMap data, EntityProperties properties) {
        data.putAll(this.data);
    }

    /**
     * Fetches an entity property.
     *
     * @param type The entity property type.
     * @return The entity property.
     */
    public <T> T get(EntityDataType<T> type) {
        return this.data.get(type);
    }

    /**
     * Sets an entity property.
     *
     * @param type The entity property type.
     * @param value The entity property value.
     */
    public <T> void set(EntityDataType<T> type, T value) {
        this.data.put(type, value);
    }
}
