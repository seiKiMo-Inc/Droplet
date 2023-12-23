package moe.seikimo.droplet.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.world.WorldFormat.ChunkPos;
import moe.seikimo.droplet.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class DropletWorld implements World {
    private final String name;
    private final long seed;

    private final Map<ChunkPos, Chunk> chunks
            = new HashMap<>();

    private long nextEntityId = 0;

    @Override
    public void addChunk(Chunk chunk) {
        this.getChunks().put(EncodingUtils.convert(
                chunk.getX(), chunk.getZ()), chunk);
    }

    @Override
    public long getNextEntityId() {
        return this.nextEntityId++;
    }
}
