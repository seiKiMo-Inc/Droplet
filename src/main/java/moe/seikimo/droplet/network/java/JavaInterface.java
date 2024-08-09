package moe.seikimo.droplet.network.java;

import org.geysermc.mcprotocollib.auth.SessionService;
import org.geysermc.mcprotocollib.network.AbstractServer;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.tcp.TcpServer;
import org.geysermc.mcprotocollib.protocol.MinecraftConstants;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.data.status.PlayerInfo;
import org.geysermc.mcprotocollib.protocol.data.status.ServerStatusInfo;
import org.geysermc.mcprotocollib.protocol.data.status.VersionInfo;
import org.geysermc.mcprotocollib.protocol.data.status.handler.ServerInfoBuilder;
import org.geysermc.mcprotocollib.protocol.packet.configuration.serverbound.ServerboundFinishConfigurationPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import org.geysermc.mcprotocollib.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.NetworkInterface;
import moe.seikimo.droplet.network.ProtocolInfo;
import moe.seikimo.droplet.network.java.handlers.InGamePacketHandler;
import moe.seikimo.droplet.network.java.handlers.LoginPacketHandler;
import moe.seikimo.handler.ObjectHandler;
import net.kyori.adventure.text.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public final class JavaInterface implements NetworkInterface {
    @Getter private final Server server;
    private final AbstractServer mcServer;
    @Getter private final ObjectHandler<JavaNetworkSession, Packet> packetHandler = new ObjectHandler<>();

    private String name, subName;

    public JavaInterface(Server server) {
        this.server = server;

        this.mcServer = new TcpServer(
                server.getIp(),
                server.getJavaPort(),
                MinecraftProtocol::new);

        // Apply server constants.
        this.mcServer.setGlobalFlag(MinecraftConstants.SESSION_SERVICE_KEY, new SessionService());
        this.mcServer.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, true);
        this.mcServer.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) this::getServerInfo);

        // Register packet handlers.
        this.getPacketHandler().register(ServerboundFinishConfigurationPacket.class, (JavaNetworkSession s, ServerboundFinishConfigurationPacket _) -> LoginPacketHandler.handle(s));
        this.getPacketHandler().register(ServerboundChatCommandPacket.class, (JavaNetworkSession s, ServerboundChatCommandPacket p) -> InGamePacketHandler.handle(s, p));

        // Add session listener.
        this.mcServer.addListener(new JavaServerAdapter(this));

        this.mcServer.bind(false);
    }

    /**
     * Prepares an info object based on server properties.
     *
     * @param session The session.
     * @return The info object.
     */
    private ServerStatusInfo getServerInfo(Session session) {
        var codec = ProtocolInfo.JAVA_CODEC;

        return new ServerStatusInfo(
                Component.text(STR."\{this.name}\n\{this.subName}"),
                new PlayerInfo(1000, this.server.getPlayerCount(), Collections.emptyList()),
                new VersionInfo(codec.getMinecraftVersion(), codec.getProtocolVersion()),
                null, false
        );
    }

    @Override
    public void blockAddress(InetAddress address) {

    }

    @Override
    public void blockAddress(InetAddress address, long timeout, TimeUnit unit) {

    }

    @Override
    public void unblockAddress(InetAddress address) {

    }

    @Override
    public void sendPacket(InetSocketAddress address, ByteBuf buffer) {

    }

    @Override
    public void setName(String name, String subName) {
        this.name = name;
        this.subName = subName;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void emergencyShutdown() {

    }
}
