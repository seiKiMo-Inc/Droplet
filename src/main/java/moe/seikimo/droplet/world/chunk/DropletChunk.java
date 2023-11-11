package moe.seikimo.droplet.world.chunk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.world.World;

@Getter
@RequiredArgsConstructor
public class DropletChunk implements Chunk {
    private final World world;
    private final int x, z;
}
