package moe.seikimo.droplet.utils;

public interface Null {
    /**
     * Returns the first non-null value.
     *
     * @param value The value to check.
     * @param defaultValue The default value.
     * @return The first non-null value.
     * @param <T> The type of the value.
     */
    static <T> T or(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
