package moe.seikimo.droplet.entity;

import lombok.Getter;

@Getter
public class DropletEntity implements Entity {
    public static long nextEntityId = 1;

    private final long entityId;

    public DropletEntity(long entityId) {
        this.entityId = entityId;
    }
}
