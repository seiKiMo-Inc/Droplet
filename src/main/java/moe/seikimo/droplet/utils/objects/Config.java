package moe.seikimo.droplet.utils.objects;

import com.google.gson.JsonObject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Config {
    private final JsonObject config = new JsonObject();

    /**
     * @see Config#read(File)
     */
    private Config() {}

    /**
     * Reads a configuration file.
     *
     * @param file The file.
     * @return The configuration.
     */
    public static Config read(File file) {
        // Check if the file exists.
        if (!file.exists()) {
            return new Config();
        }

        var parts = file.getName().split("\\.");
        if (parts.length == 1) return new Config();

        return switch (parts[parts.length - 1]) {
            default -> throw new IllegalArgumentException(STR."\{file.getName()} is not a valid configuration.");
            case "properties" -> Config.readProperties(file);
        };
    }

    /**
     * Parses a map into a configuration.
     *
     * @param value The map.
     * @param type The configuration type.
     * @return The configuration.
     */
    public static Config read(Map<String, String> value, ConfigType type) {
        // Parse the map into a list of KEY=VALUE strings.
        var content = new ArrayList<String>();
        for (var entry : value.entrySet()) {
            content.add(STR."\{entry.getKey()}=\{entry.getValue()}");
        }

        if (type != ConfigType.PROPERTIES) {
            throw new IllegalArgumentException("Unsupported configuration type.");
        }

        return Config.readProperties(content);
    }

    /**
     * Reads a properties file.
     *
     * @param file The file.
     * @return The configuration.
     */
    private static Config readProperties(File file) {
        try {
            return Config.readProperties(
                    Files.readAllLines(file.toPath()));
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Reads a properties file.
     *
     * @param content The properties file's contents.
     * @return The configuration.
     */
    private static Config readProperties(List<String> content) {
        var config = new Config();

        try {
            // Read the file's contents.
            for (var line : content) {
                // Skip comments.
                if (line.startsWith("#")) continue;

                // Split the line into key and value.
                var parts = line.split("=");
                if (parts.length != 2) continue;

                // Add the key and value to the configuration.
                config.config.addProperty(parts[0], parts[1]);
            }
        } catch (Exception exception) {
            return null;
        }

        return config;
    }

    /**
     * Gets a string from the configuration.
     *
     * @param key The key.
     * @return The string.
     */
    public String getString(String key) {
        return this.config.get(key).getAsString();
    }

    /**
     * Gets a string from the configuration.
     *
     * @param key The key.
     * @param fallback The fallback value.
     * @return The string.
     */
    public String getString(String key, String fallback) {
        try {
            return this.config.get(key).getAsString();
        } catch (Exception exception) {
            return fallback;
        }
    }

    /**
     * Gets an integer from the configuration.
     *
     * @param key The key.
     * @return The integer.
     */
    public int getInt(String key) {
        return this.config.get(key).getAsInt();
    }

    /**
     * Gets a string from the configuration.
     *
     * @param key The key.
     * @param fallback The fallback value.
     * @return The string.
     */
    public int getInt(String key, int fallback) {
        try {
            return this.config.get(key).getAsInt();
        } catch (Exception exception) {
            return fallback;
        }
    }

    /**
     * Gets a boolean from the configuration.
     *
     * @param key The key.
     * @return The boolean.
     */
    public boolean getBoolean(String key) {
        return this.config.get(key).getAsBoolean();
    }

    /**
     * Gets a boolean from the configuration.
     *
     * @param key The key.
     * @param fallback The fallback value.
     * @return The string.
     */
    public boolean getBoolean(String key, boolean fallback) {
        try {
            return this.config.get(key).getAsBoolean();
        } catch (Exception exception) {
            return fallback;
        }
    }

    /**
     * All known configuration types.
     */
    public enum ConfigType {
        PROPERTIES,
        JSON,
        YAML
    }
}
