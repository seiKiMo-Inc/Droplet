package moe.seikimo.droplet.entity;

public interface Entity {
    /**
     * @return The entity's runtime ID.
     */
    long getEntityId();

    /**
     * @return The entity's metadata and flags.
     */
    EntityData getData();
}
