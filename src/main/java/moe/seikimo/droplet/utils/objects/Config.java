package moe.seikimo.droplet.utils.objects;

import com.google.gson.JsonObject;

import java.io.File;
import java.nio.file.Files;

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
            default -> throw new IllegalArgumentException(file.getName() + " is not a valid configuration.");
            case "properties" -> Config.readProperties(file);
        };
    }

    /**
     * Reads a properties file.
     *
     * @param file The file.
     * @return The configuration.
     */
    private static Config readProperties(File file) {
        var config = new Config();

        try {
            // Read the file's contents.
            var content = Files.readAllLines(file.toPath());
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
}
