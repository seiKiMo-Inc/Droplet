package moe.seikimo.droplet;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.seikimo.droplet.commands.BlockCommand;
import moe.seikimo.droplet.commands.CommandSource;
import org.geysermc.mcprotocollib.network.packet.Packet;
import lombok.Getter;
import lombok.Setter;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.entity.DropletEntity;
import moe.seikimo.droplet.item.ItemManager;
import moe.seikimo.droplet.network.NetworkManager;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.network.bedrock.BedrockInterface;
import moe.seikimo.droplet.network.java.JavaInterface;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.network.shared.play.DropletPlayerListPacket;
import moe.seikimo.droplet.player.DropletSkin;
import moe.seikimo.droplet.player.Player;
import moe.seikimo.droplet.player.data.DeviceInfo;
import moe.seikimo.droplet.utils.Log;
import moe.seikimo.droplet.utils.objects.Config;
import moe.seikimo.droplet.world.World;
import moe.seikimo.droplet.world.biome.Biome;
import moe.seikimo.droplet.world.io.AnvilFormatReader;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class Server extends CommandSource {
    /**
     * Creates a new server instance.
     */
    public static void initialize() {
        var server = Server.instance = new Server();

        // Load server data.
        BlockPalette.load();
        ItemManager.load();
        Biome.load();
        DropletEntity.loadIdentifiers();

        // Register commands.
        var dispatcher = server.getDispatcher();
        BlockCommand.register(dispatcher);
    }

    @Getter private static Server instance;
    @Getter private static boolean isDebug = false;

    @Getter private final Logger logger
            = LoggerFactory.getLogger("Server");
    private final NetworkManager networkManager
            = new NetworkManager(this);

    @Getter private final List<NetworkSession> sessions
            = Collections.synchronizedList(new ArrayList<>());
    @Getter private final List<Player> onlinePlayers
            = new ArrayList<>();

    @Getter private final Map<UUID, PlayerListPacket.Entry> playerList
            = new ConcurrentHashMap<>();

    @Getter private final Config config
            = Config.read(new File("server.properties"));
    @Getter private final CommandDispatcher<CommandSource> dispatcher
            = new CommandDispatcher<>();

    @Getter @Setter private World defaultWorld;
    @Getter @Setter private ItemManager itemManager;

    @Getter @Setter private String ip = "0.0.0.0";
    @Getter @Setter private short bedrockPort = 19132;
    @Getter @Setter private short javaPort = 25565;

    @Getter @Setter private boolean logPackets = false;

    /**
     * Starts the server.
     */
    public void start() {
        // Check if the config is valid.
        if (this.config == null) {
            throw new RuntimeException("No configuration detected.");
        }

        // Enable debug mode.
        Server.isDebug = this.getConfig().getBoolean("server.debug", false);
        Log.setDebug(this.getLogger());

        // Enable packet logging.
        this.setLogPackets(this.config.getBoolean("server.log_packets", false));
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
            // this.getLogger().info("Preparing world {}...",
            //         this.getDefaultWorld().getName());

            // var worldReader = new DropletFormatReader();
            // this.setDefaultWorld(worldReader.read(new File("world.droplet")));

            var reader = new AnvilFormatReader();
            var world = reader.read(new File("worlds/world"));
            this.setDefaultWorld(world);
        } catch (IOException exception) {
            this.getLogger().error("Failed to read default world file.", exception);
            System.exit(1);
        } catch (Exception exception) {
            this.getLogger().error("Failed to load default world.", exception);
            System.exit(1);
        }
    }

    /// <editor-fold desc="Network Methods">

    /**
     * Broadcasts a collection of Bedrock packets to all Bedrock clients.
     *
     * @param packets The packets to broadcast.
     */
    public void broadcastPacket(BedrockPacket... packets) {
        for (var session : this.getSessions()) {
            if (!session.getDevice().isBedrock()) continue;
            Arrays.stream(packets).forEach(session::sendPacket);
        }
    }

    /**
     * Broadcasts a collection of Java packets to all Java clients.
     *
     * @param packets The packets to broadcast.
     */
    public void broadcastPacket(Packet... packets) {
        for (var session : this.getSessions()) {
            if (session.getDevice().isBedrock()) continue;
            Arrays.stream(packets).forEach(session::sendPacket);
        }
    }

    /**
     * Broadcasts a packet to all clients.
     *
     * @param packet The packet to broadcast.
     */
    public void broadcastPacket(BasePacket packet) {
        this.getSessions().forEach(s -> s.sendPacket(packet));
    }

    /// </editor-fold>

    /// <editor-fold desc="Player Management">

    /**
     * Adds a player to the server list.
     * TODO: Add support for Java-specific list fields.
     *
     * @param player The player to add.
     */
    public void addPlayerToList(Player player) {
        this.addPlayerToList(
                player.getUuid(),
                player.getEntityId(),
                player.getDisplayName(),
                player.getDeviceInfo(),
                player.getXuid(),
                player.getSkin()
        );
    }

    /**
     * Adds a player to the server list of players.
     */
    public void addPlayerToList(
            UUID uuid, long entityId,
            String name, DeviceInfo deviceInfo,
            String xuid, DropletSkin skinData
    ) {
        var entry = DropletPlayerListPacket.Entry.builder()
                .uuid(uuid).entityId(entityId)
                .name(name).deviceInfo(deviceInfo)
                .xuid(xuid).skin(skinData)
                .build();
        var listPacket = new DropletPlayerListPacket(
                DropletPlayerListPacket.Action.ADD, List.of(entry)
        );

        // Broadcast the packet.
        this.broadcastPacket(listPacket);
    }

    /**
     * @return The amount of players online.
     */
    public int getPlayerCount() {
        return this.onlinePlayers.size();
    }

    /// </editor-fold>

    /// <editor-fold desc="Commands">

    @Override
    public Server asServer() {
        return this;
    }

    @Override
    public void sendMessage(String message) {
        this.getLogger().info(message);
    }

    /**
     * Executes a command as the server.
     *
     * @param command The command to execute.
     */
    public void executeCommand(String command) throws CommandSyntaxException {
        this.getDispatcher().execute(command, this);
    }

    /// </editor-fold>
}
