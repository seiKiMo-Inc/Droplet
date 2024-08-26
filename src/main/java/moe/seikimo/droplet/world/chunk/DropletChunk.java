package moe.seikimo.droplet.world.chunk;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.common.util.VarInts;
import org.geysermc.mcprotocollib.protocol.data.game.level.LightUpdateData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moe.seikimo.droplet.entity.Entity;
import moe.seikimo.droplet.utils.EncodingUtils;
import org.cloudburstmc.nbt.NbtMap;

import java.util.BitSet;
import java.util.List;

@Slf4j
@Getter
@RequiredArgsConstructor
public final class DropletChunk implements Chunk {
    public static final byte VERSION = 9;
    private static final int WORLD_HEIGHT = 384; // (-4 -> 19) * 16

    private static final byte[] EMPTY_JAVA_CHUNK_DATA;

    static {
        var buffer = Unpooled.buffer();
        try {
            buffer.writeShort(0); // Non-air block count.
            buffer.writeByte(0); // bits per entry
            buffer.writeByte(0); // air block state id
            buffer.writeByte(0); // long array len

            // Biome palette.
            buffer.writeByte(0); // bits per entry
            buffer.writeByte(0x27); // biome entry
            buffer.writeByte(0); // indexed data

            EMPTY_JAVA_CHUNK_DATA = new byte[buffer.readableBytes()];
            buffer.readBytes(EMPTY_JAVA_CHUNK_DATA);
        } finally {
            buffer.release();
        }
    }

    private final int x, z;

    private final Long2ObjectMap<Entity> entities
            = new Long2ObjectOpenHashMap<>();
    private final ChunkSection[] sections
            = new ChunkSection[24];

    @Getter @Setter
    private NbtMap heightMaps;

    @Override
    public int getSectionCount() {
        var empty = WORLD_HEIGHT / 16;
        for (var ci = empty - 1; ci >= 0; ci--) {
            var section = this.sections[ci];
            if (section == null || section.isEmpty()) {
                empty = ci;
            } else {
                break;
            }
        }
        return empty;
    }

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
        if (index < -4 || index > 19) {
            throw new IndexOutOfBoundsException(
                    "Index must be between -4 and 19, inclusive.");
        }

        return this.sections[index + 4];
    }

    @Override
    public void addEntity(Entity entity) {
        var entityId = entity.getEntityId();
        this.entities.put(entityId, entity);
    }

    @Override
    public ByteBuf encodeBedrock() {
        // TODO: Cache this.

        var buffer = Unpooled.buffer();

        // Write chunk sections.
        var sectionCount = this.getSectionCount();
        for (var i = 0; i < sectionCount; i++) {
            var section = this.getSectionByIndex(i - 4);
            if (section == null) {
                buffer.writeByte(DropletChunkSection.SECTION_VERSION); // Version.
                buffer.writeByte(0); // Storage count.
                buffer.writeByte(i); // Layer number.
            } else {
                buffer.writeBytes(section.encodeBedrock());
            }
        }

        // Write biome data.
        // Taken from `@serenityjs/world`
        for (var i = 0; i < 24; i++) {
            buffer.writeByte(0);
            VarInts.writeInt(buffer, 1 << 1);
        }

        // Border block data.
        buffer.writeByte(0);

        // TODO: Write block entities.

        return buffer;
    }

    @Override
    public byte[] encodeJava() {
        var buffer = Unpooled.buffer();

        for (var i = -4; i < 20; i++) try {
            var section = this.getSectionByIndex(i);
            if (section == null) {
                section = new DropletChunkSection(i);
            }

            buffer.writeBytes(section.encodeJava());
        } catch (Exception exception) {
            log.error("Unable to encode Java chunk section.", exception);
        }

        return EncodingUtils.toBytes(buffer);
    }

    @Override
    public LightUpdateData getJavaLightData() {
        var empty = new BitSet();

        // TODO: Compute proper world lighting.
        return new LightUpdateData(
                empty, empty, empty, empty,
                List.of(), List.of()
        );
    }
}
