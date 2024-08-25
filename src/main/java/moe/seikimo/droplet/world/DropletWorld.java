package moe.seikimo.droplet.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.block.BlockState;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.world.WorldFormat.ChunkPos;
import moe.seikimo.droplet.world.chunk.Chunk;
import org.cloudburstmc.protocol.common.util.Preconditions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
public final class DropletWorld implements World {
    private final String name;
    private final long seed;

    private final Map<ChunkPos, Chunk> chunks
            = new ConcurrentHashMap<>();

    @Override
    public void addChunk(Chunk chunk) {
        this.getChunks().put(EncodingUtils.convert(
                chunk.getX(), chunk.getZ()), chunk);
    }

    @Override
    public Chunk getChunkAt(int x, int z) {
        return this.getChunks().get(EncodingUtils.convert(x, z));
    }

    @Override
    public BlockState getBlockAt(int x, int y, int z) {
        var chunk = this.getChunkAt(x >> 4, z >> 4);
        Preconditions.checkNotNull(chunk, "Chunk is not loaded.");

        var section = chunk.getSectionByIndex(y >> 4);
        Preconditions.checkNotNull(section, "Section is not loaded.");

        var block = section.getBlockAt(x & 0xF, y & 0xF, z & 0xF);
        return BlockPalette.getPalette().get(block);
    }
}
