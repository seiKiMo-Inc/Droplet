package moe.seikimo.droplet.world.block;

public final class DropletBlock implements Block {
    /**
     * Creates a new block from a block state.
     *
     * @param blockState The block state.
     * @return The block.
     */
    public static DropletBlock fromState(int blockState) {
        return new DropletBlock();
    }
}
