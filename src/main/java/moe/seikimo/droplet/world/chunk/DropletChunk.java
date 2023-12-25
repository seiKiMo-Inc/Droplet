package moe.seikimo.droplet.world.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.block.BlockStorage;
import moe.seikimo.droplet.entity.Entity;
import moe.seikimo.droplet.utils.objects.binary.SingletonBitArray;

@Getter
@RequiredArgsConstructor
public final class DropletChunk implements Chunk {
    private static final byte VERSION = 9;
    private static final byte[] EMPTY_BIOME_DATA;

    static {
        var buffer = Unpooled.buffer();
        try {
            var storage = new BlockStorage(SingletonBitArray.INSTANCE, IntLists.singleton(0));
            storage.serialize(buffer);

            EMPTY_BIOME_DATA = new byte[buffer.readableBytes()];
            buffer.readBytes(EMPTY_BIOME_DATA);
        } finally {
            buffer.release();
        }
    }

    private final int x, z;

    private final Long2ObjectMap<Entity> entities
            = new Long2ObjectOpenHashMap<>();
    private final ChunkSection[] sections
            = new ChunkSection[24];

    @Override
    public void setSection(int section, ChunkSection chunkSection) {
        if (section < -4 || section > 19) {
            throw new IndexOutOfBoundsException(
                    "Section must be between -4 and 19, inclusive. Value: " + section);
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

    @Override
    public ByteBuf encodeBedrock() {
        var buffer = Unpooled.buffer();

        // Write chunk sections.
        for (var section : this.getSections()) {
            if (section == null) {
                buffer.writeBytes(EMPTY_BIOME_DATA);
            } else {
                buffer.writeBytes(section.encodeBedrock());
            }
        }

        // Write biome data.
        buffer.writeBytes(EMPTY_BIOME_DATA);

        // Border block data.
        buffer.writeByte(0);

        return buffer;
    }

    @Override
    public byte[] encodeJava() {
        return new byte[0];
    }
}
