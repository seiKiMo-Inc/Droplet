package moe.seikimo.droplet.world.chunk.section;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import moe.seikimo.droplet.block.Block;

public interface ChunkSection {
    /**
     * @return The X coordinate of this chunk section.
     */
    int getY();

    /**
     * Gets a block in the section at the coordinates.
     * These are not world coordinates, rather they are chunk section coordinates.
     *
     * @param x The X coordinate of the block. (0-16)
     * @param y The Y coordinate of the block. (0-16)
     * @param z The Z coordinate of the block. (0-16)
     * @return The block.
     */
    Block getBlockAt(int x, int y, int z);

    /**
     * Sets a block in the section at the coordinates.
     *
     * @param x The X coordinate of the block. (0-16)
     * @param y The Y coordinate of the block. (0-16)
     * @param z The Z coordinate of the block. (0-16)
     * @param paletteIndex The index of the block in the palette.
     */
    void setBlockAt(int x, int y, int z, int paletteIndex);

    /**
     * @return The encoded chunk section.
     */
    ByteBuf encodeBedrock();

    /**
     * @return The encoded chunk section.
     */
    byte[] encodeJava();
}
