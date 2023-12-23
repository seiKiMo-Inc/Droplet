package moe.seikimo.droplet.data.types;

import java.util.HashMap;
import java.util.Map;

public record MinecraftBlock(
        String name,
        Map<String, String> properties
) {
    /**
     * Converts the string of a block to a MinecraftBlock object.
     *
     * @param string The string.
     * @return The MinecraftBlock object.
     */
    public static MinecraftBlock fromString(String string) {
        var split = string.split("\\[");
        var name = split[0];
        var properties = split[1].substring(0, split[1].length() - 1);

        // Parse properties.
        var map = new HashMap<String, String>();
        for (var property : properties.split(",")) {
            var keyValue = property.split("=");
            map.put(keyValue[0], keyValue[1]);
        }

        return new MinecraftBlock(name, map);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder(this.name());
        if (!this.properties().isEmpty()) {
            builder.append('[');
            this.properties().forEach((key, value) -> builder
                    .append(key).append('=').append(value).append(','));
            builder.deleteCharAt(builder.length() - 1);
            builder.append(']');
        }

        return builder.toString();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
