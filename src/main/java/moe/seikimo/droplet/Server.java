package moe.seikimo.droplet;

import lombok.Getter;
import lombok.Setter;
import moe.seikimo.droplet.network.NetworkManager;
import moe.seikimo.droplet.network.bedrock.BedrockInterface;
import moe.seikimo.droplet.network.java.JavaInterface;
import moe.seikimo.droplet.world.io.AnvilRegionFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Server {
    /**
     * Creates a new server instance.
     */
    public static void initialize() {
        Server.instance = new Server();
    }

    @Getter private static Server instance;

    @Getter private final Logger logger
            = LoggerFactory.getLogger("Server");
    private final NetworkManager networkManager
            = new NetworkManager(this);
    @Getter private final List<Object> onlinePlayers
            = new ArrayList<>();

    @Getter @Setter private String ip = "0.0.0.0";
    @Getter @Setter private short bedrockPort = 19132;
    @Getter @Setter private short javaPort = 25565;

    /**
     * Starts the server.
     */
    public void start() {
        // Register network interfaces.
        this.networkManager.registerInterface(new BedrockInterface(this));
        this.networkManager.registerInterface(new JavaInterface(this));
    }

    /**
     * @return The amount of players online.
     */
    public int getPlayerCount() {
        return this.onlinePlayers.size();
    }
}
