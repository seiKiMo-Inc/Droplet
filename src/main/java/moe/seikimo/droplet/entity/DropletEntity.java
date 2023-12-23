package moe.seikimo.droplet.entity;

import lombok.Getter;

@Getter
public class DropletEntity implements Entity {
    private final long entityId;

    public DropletEntity(long entityId) {
        this.entityId = entityId;
    }
}
