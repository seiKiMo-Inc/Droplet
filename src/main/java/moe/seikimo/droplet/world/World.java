package moe.seikimo.droplet.world;

import moe.seikimo.droplet.world.WorldFormat.ChunkPos;
import moe.seikimo.droplet.world.chunk.Chunk;

import java.util.Map;

public interface World {
    /**
     * @return The display name of the world.
     */
    String getName();

    /**
     * @return The world's seed as a 64-bit integer.
     */
    long getSeed();

    /**
     * This method will always return every chunk, regardless of it's fully loaded in memory or not.
     *
     * @return All chunks in the world.
     */
    Map<ChunkPos, Chunk> getChunks();

    /**
     * Gets a chunk at the given coordinates.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The chunk at the given coordinates.
     */
    Chunk getChunkAt(int x, int z);

    /**
     * Adds a chunk to the world.
     *
     * @param chunk The chunk to add.
     */
    void addChunk(Chunk chunk);
}
