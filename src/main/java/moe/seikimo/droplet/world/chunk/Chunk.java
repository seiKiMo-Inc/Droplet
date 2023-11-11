package moe.seikimo.droplet.world.chunk;

import moe.seikimo.droplet.world.World;

public interface Chunk {
    /**
     * @return The chunk's X coordinate.
     */
    int getX();

    /**
     * @return The chunk's Z coordinate.
     */
    int getZ();

    /**
     * @return The world handle of this chunk.
     */
    World getWorld();
}
