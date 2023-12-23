package moe.seikimo.droplet.data.types;

import com.google.gson.JsonObject;
import org.cloudburstmc.nbt.NbtMap;

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

        var map = new HashMap<String, String>();
        if (split.length != 1) {
            var properties = split[1].substring(0, split[1].length() - 1);

            // Parse properties.
            for (var property : properties.split(",")) {
                var keyValue = property.split("=");
                if (keyValue.length != 1) {
                    map.put(keyValue[0], propertyParser(keyValue[1]));
                }
            }
        }
wa
        return new MinecraftBlock(name, map);
    }

    /**
     * Converts a BedrockBlock to a MinecraftBlock.
     *
     * @param block The BedrockBlock.
     * @return The MinecraftBlock.
     */
    public static MinecraftBlock fromBedrock(BedrockBlock block) {
        var properties = new HashMap<String, String>();
        block.states().forEach((key, value) ->
                properties.put(key.replaceAll("minecraft:", ""),
                        propertyParser(value.toString())));

        return new MinecraftBlock(block.prefixedName(), properties);
    }

    /**
     * Converts a MinecraftBlock to a BedrockBlock.
     *
     * @param identifier The identifier.
     * @param state The state.
     * @return The BedrockBlock.
     */
    public static MinecraftBlock fromJson(String identifier, JsonObject state) {
        var properties = new HashMap<String, String>();
        state.entrySet().forEach(entry ->
                properties.put(entry.getKey(),
                        propertyParser(entry.getValue().getAsString())));

        return new MinecraftBlock(identifier, properties);
    }

    /**
     * Parses a property.
     *
     * @param value The property's value.
     * @return The parsed property.
     */
    public static String propertyParser(String value) {
        if (value.equals("true")) {
            return "1";
        } else if (value.equals("false")) {
            return "0";
        } else {
            return value;
        }
    }

    /**
     * @return The NBT map of the properties.
     */
    public NbtMap propertiesNbt() {
        var map = NbtMap.builder();
        this.properties().forEach(map::putString);
        return map.build();
    }

    @Override
    public String toString() {
        var builder = new StringBuilder(this.name());
        if (!this.properties().isEmpty()) {
            builder.append('[');

            // Sort properties by name.
            var sorted = new HashMap<String, String>();
            this.properties().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(String::compareTo))
                    .forEach(entry -> sorted.put(entry.getKey(), entry.getValue()));
            // Append properties.
            sorted.forEach((key, value) -> builder
                    .append(key).append('=').append(value).append(','));

            builder.deleteCharAt(builder.length() - 1);
            builder.append(']');
        }

        return builder.toString();
    }

    @Override
    public int hashCode() {
        return this.properties().hashCode() + this.name().hashCode();
    }
}
