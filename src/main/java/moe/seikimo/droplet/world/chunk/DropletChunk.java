package moe.seikimo.droplet.world.chunk;

import com.github.steveice10.mc.protocol.data.game.level.LightUpdateData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moe.seikimo.droplet.block.BlockStorage;
import moe.seikimo.droplet.entity.Entity;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.utils.objects.binary.SingletonBitArray;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.util.BitSet;
import java.util.List;

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

    @Getter @Setter
    private NbtMap heightMaps;

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
        var buffer = Unpooled.buffer();

        {
            // Write chunk sections.
            for (var section : this.getSections()) {
                if (section == null) {
                    // TODO: Write empty chunk section data.
                    throw new UnsupportedOperationException("Empty chunk sections are not supported.");
                } else {
                    buffer.writeBytes(section.encodeJava());
                }
            }
        }

        {
            // Write biomes to the buffer.
            for (var z = 0; z < 16; z++) {
                for (var x = 0; x < 16; x++) {
                    buffer.writeByte(127);
                }
            }
        }

        {
            // Write block entities to the buffer.
            VarInts.writeInt(buffer, 0);
            // TODO: Write block entities to the Java buffer.
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
