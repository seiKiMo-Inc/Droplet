package moe.seikimo.droplet.world.chunk;

import io.netty.buffer.ByteBuf;

public interface ChunkSection {
    /**
     * @return The X coordinate of this chunk section.
     */
    int getY();

    /**
     * @return True if the chunk section has no blocks.
     */
    boolean isEmpty();

    /**
     * Gets a block in the section at the coordinates.
     * These are not world coordinates, rather they are chunk section coordinates.
     *
     * @param x The X coordinate of the block. (0-16)
     * @param y The Y coordinate of the block. (0-16)
     * @param z The Z coordinate of the block. (0-16)
     * @return The block's ID.
     */
    int getBlockAt(int x, int y, int z);

    /**
     * Sets a block in the section at the coordinates.
     *
     * @param x The X coordinate of the block. (0-16)
     * @param y The Y coordinate of the block. (0-16)
     * @param z The Z coordinate of the block. (0-16)
     * @param paletteIndex The block ID of the block.
     */
    void setBlockAt(int x, int y, int z, int paletteIndex);

    /**
     * @return The encoded chunk section.
     */
    ByteBuf encodeBedrock();

    /**
     * @return The encoded chunk section.
     */
    ByteBuf encodeJava();
}
