package moe.seikimo.droplet;

import lombok.Getter;
import lombok.Setter;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.item.ItemManager;
import moe.seikimo.droplet.network.NetworkManager;
import moe.seikimo.droplet.network.bedrock.BedrockInterface;
import moe.seikimo.droplet.network.java.JavaInterface;
import moe.seikimo.droplet.utils.objects.Config;
import moe.seikimo.droplet.world.World;
import moe.seikimo.droplet.world.biome.Biome;
import moe.seikimo.droplet.world.io.DropletFormatReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Server {
    /**
     * Creates a new server instance.
     */
    public static void initialize() {
        Server.instance = new Server();

        // Load server data.
        BlockPalette.load();
        ItemManager.load();
        Biome.load();
    }

    @Getter private static Server instance;

    @Getter private final Logger logger
            = LoggerFactory.getLogger("Server");
    private final NetworkManager networkManager
            = new NetworkManager(this);
    @Getter private final List<Object> onlinePlayers
            = new ArrayList<>();
    @Getter private final Config config
            = Config.read(new File("server.properties"));

    @Getter @Setter private World defaultWorld;
    @Getter @Setter private ItemManager itemManager;

    @Getter @Setter private String ip = "0.0.0.0";
    @Getter @Setter private short bedrockPort = 19132;
    @Getter @Setter private short javaPort = 25565;

    /**
     * Starts the server.
     */
    public void start() {
        // Check if the config is valid.
        if (this.config == null) {
            throw new RuntimeException("No configuration detected.");
        }

        // Register network interfaces.
        this.networkManager.registerInterface(new BedrockInterface(this));
        this.networkManager.registerInterface(new JavaInterface(this));

        // Load the default world.
        this.loadDefaultWorld();
    }

    /**
     * Loads the default world from disk.
     */
    public void loadDefaultWorld() {
        try {
            var worldReader = new DropletFormatReader();
            this.setDefaultWorld(worldReader.read(new File("world.droplet")));

            this.getLogger().info("Preparing world {}...",
                    this.getDefaultWorld().getName());
        } catch (IOException exception) {
            this.getLogger().error("Failed to read default world file.", exception);
            System.exit(1);
        } catch (Exception exception) {
            this.getLogger().error("Failed to load default world.", exception);
            System.exit(1);
        }
    }

    /**
     * @return The amount of players online.
     */
    public int getPlayerCount() {
        return this.onlinePlayers.size();
    }
}
