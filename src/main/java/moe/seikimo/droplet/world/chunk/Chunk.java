package moe.seikimo.droplet.world.chunk;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import moe.seikimo.droplet.entity.Entity;

public interface Chunk {
    /**
     * @return The X coordinate of this chunk.
     */
    int getX();

    /**
     * @return The Y coordinate of this chunk.
     */
    int getZ();

    /**
     * @return All entities in this chunk.
     */
    Long2ObjectMap<Entity> getEntities();

    /**
     * @return All sections of this chunk.
     */
    ChunkSection[] getSections();

    /**
     * Sets a section at the given index.
     *
     * @param section The index of the section.
     * @param chunkSection The section to set.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    void setSection(int section, ChunkSection chunkSection);

    /**
     * Fetches a section by its index.
     *
     * @param index The index of the section.
     * @return The section at the given index.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    ChunkSection getSectionByIndex(int index) throws IndexOutOfBoundsException;

    /**
     * Adds an entity to this chunk.
     *
     * @param entity The entity to add.
     */
    void addEntity(Entity entity);

    /**
     * @return The chunk encoded for the Bedrock format.
     */
    ByteBuf encodeBedrock();

    /**
     * @return The chunk encoded for the Java format.
     */
    byte[] encodeJava();
}
