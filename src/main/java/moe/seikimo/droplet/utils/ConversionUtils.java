package moe.seikimo.droplet.utils;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.opennbt.tag.builtin.*;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.GameType;

public interface ConversionUtils {
    /**
     * Converts a Java GameMode to a Bedrock GameType.
     *
     * @param gameMode The Java GameMode to convert.
     * @return The Bedrock GameType equivalent to the Java GameMode.
     */
    static GameType convert(GameMode gameMode) {
        return GameType.valueOf(gameMode.name());
    }

    /**
     * Converts a Cloudburst NBT tag to an OpenNBT tag.
     *
     * @param name The name of the NBT tag.
     * @param value The NBT tag to convert.
     * @return The OpenNBT tag equivalent to the NBT tag.
     */
    static Tag convertNbt(String name, Object value) {
        if (value instanceof NbtMap map) return convert(map);
        if (value instanceof byte[] bytes) return new ByteArrayTag(name, bytes);
        if (value instanceof String str) return new StringTag(name, str);
        if (value instanceof Integer number) return new IntTag(name, number);
        if (value instanceof Float decimal) return new FloatTag(name, decimal);
        if (value instanceof Long number) return new LongTag(name, number);
        if (value instanceof Short number) return new ShortTag(name, number);
        if (value instanceof Double decimal) return new DoubleTag(name, decimal);
        if (value instanceof Byte aByte) return new ByteTag(name, aByte);
        if (value instanceof int[] ints) return new IntArrayTag(name, ints);
        if (value instanceof long[] longs) return new LongArrayTag(name, longs);

        throw new IllegalArgumentException("Unknown NBT type: " + value.getClass().getName());
    }

    /**
     * Converts an NBTMap to a CompoundTag.
     *
     * @param map The NBTMap to convert.
     * @return The CompoundTag equivalent to the NBTMap.
     */
    static CompoundTag convert(NbtMap map) {
        var tag = new CompoundTag("");
        for (var entry : map.entrySet()) {
            tag.put(convertNbt(entry.getKey(), entry.getValue()));
        }
        return tag;
    }
}
