package moe.seikimo.droplet.world.biome;

import lombok.Getter;
import moe.seikimo.droplet.Droplet;
import moe.seikimo.droplet.utils.FileUtils;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;

public final class Biome {
    private static final String DEFINITIONS_PATH = "data/biome_definitions.dat";

    @Getter private static NbtMap biomeDefinitions;

    /**
     * Loads biome data.
     */
    public static void load() {
        try {
            Droplet.getLogger().info("Loading biome definitions...");

            var stream = NbtUtils.createNetworkReader(
                    FileUtils.resource(DEFINITIONS_PATH));
            Biome.biomeDefinitions = (NbtMap) stream.readTag();

            stream.close();
            Droplet.getLogger().debug("Loaded {} biome definitions.", biomeDefinitions.size());
        } catch (Exception exception) {
            Droplet.getLogger().warn("Failed to load biome definitions.", exception);
        }
    }
}
