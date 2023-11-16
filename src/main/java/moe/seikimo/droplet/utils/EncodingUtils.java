package moe.seikimo.droplet.utils;

import com.google.gson.Gson;

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
}
