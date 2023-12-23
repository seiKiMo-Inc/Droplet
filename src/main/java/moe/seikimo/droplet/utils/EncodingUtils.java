package moe.seikimo.droplet.utils;

import com.google.gson.Gson;
import moe.seikimo.droplet.world.WorldFormat;
import moe.seikimo.droplet.world.WorldFormat.ChunkPos;
import org.cloudburstmc.math.vector.Vector3i;

import java.util.Base64;

public interface EncodingUtils {
    Gson GSON = new Gson();

    /**
     * Decodes a Base64 string into a byte array.
     *
     * @param base64 The Base64 string to decode.
     * @return The decoded byte array.
     */
    static byte[] base64Decode(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * Decodes a Base64 string into a Java object.
     *
     * @param base64 The Base64 string to decode.
     * @param type The type of the Java object.
     * @return The decoded Java object.
     */
    static <T> T base64Decode(String base64, Class<T> type) {
        return EncodingUtils.GSON.fromJson(new String(
                EncodingUtils.base64Decode(base64)), type);
    }

    /**
     * Converts a Java ChunkPos to a Bedrock ChunkPos.
     *
     * @param x The x coordinate of the chunk.
     * @param z The z coordinate of the chunk.
     * @return A chunk position object.
     */
    static ChunkPos convert(int x, int z) {
        return ChunkPos.newBuilder()
                .setX(x).setZ(z)
                .build();
    }

    /**
     * Encodes a location to the Droplet format.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @return The encoded location.
     */
    static int encodePosition(int x, int y, int z) {
        return ((x & 0x3FFFFFF) << 6) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }

    /**
     * Decodes a location from the Droplet format.
     *
     * @param encoded The encoded location.
     * @return The decoded location.
     */
    static Vector3i decodePosition(int encoded) {
        var x = (encoded >> 6) & 0x3FFFFFF;
        var z = (encoded >> 12) & 0x3FFFFFF;
        var y = encoded & 0xFFF;

        return Vector3i.from(x, y, z);
    }

    /**
     * Compresses an X, Y, Z coordinate into a single index.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The index.
     */
    static int getIndex(int x, int y, int z) {
        return ((x << 8) + (z << 4)) | y;
    }
}
