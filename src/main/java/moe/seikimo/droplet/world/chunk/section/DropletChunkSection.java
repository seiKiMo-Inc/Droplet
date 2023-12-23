package moe.seikimo.droplet.world.chunk.section;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.world.block.Block;
import moe.seikimo.droplet.world.block.DropletBlock;

@Getter
@RequiredArgsConstructor
public final class DropletChunkSection implements ChunkSection {
    private final int y;

    private final Int2IntMap
            palette = new Int2IntArrayMap(),
            blockStates = new Int2IntArrayMap();

    @Override
    public Block getBlockAt(int x, int y, int z) {
        var index = EncodingUtils.getIndex(x, y, z);
        var state = this.getBlockStates().get(index);

        return DropletBlock.fromState(state);
    }

    @Override
    public void setBlockAt(int x, int y, int z, int paletteIndex) {
        var index = EncodingUtils.getIndex(x, y, z);
        this.getBlockStates().put(index, paletteIndex);
    }
}
