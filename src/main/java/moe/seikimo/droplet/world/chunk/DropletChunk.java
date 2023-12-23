package moe.seikimo.droplet.world.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.entity.Entity;
import moe.seikimo.droplet.world.chunk.section.ChunkSection;

@Getter
@RequiredArgsConstructor
public final class DropletChunk implements Chunk {
    private final int x, z;

    private final Long2ObjectMap<Entity> entities
            = new Long2ObjectOpenHashMap<>();
    private final ChunkSection[] sections
            = new ChunkSection[24];

    @Override
    public void setSection(int section, ChunkSection chunkSection) {
        if (section < -4 || section > 15) {
            throw new IndexOutOfBoundsException(
                    "Section must be between -4 and 15, inclusive.");
        }

        this.sections[section + 4] = chunkSection;
    }

    @Override
    public ChunkSection getSectionByIndex(int index)
            throws IndexOutOfBoundsException {
        if (index < -4 || index > 15) {
            throw new IndexOutOfBoundsException(
                    "Index must be between -4 and 15, inclusive.");
        }

        return sections[index + 4];
    }

    @Override
    public void addEntity(Entity entity) {
        var entityId = entity.getEntityId();
        this.entities.put(entityId, entity);
    }
}
