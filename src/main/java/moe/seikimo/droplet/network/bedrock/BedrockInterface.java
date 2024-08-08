package moe.seikimo.droplet.network.bedrock;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.NetworkInterface;
import moe.seikimo.droplet.network.bedrock.handlers.LoginPacketHandler;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class BedrockInterface implements NetworkInterface {
    private final Server server;

    private final List<Channel> channels = new ArrayList<>();
    private final BedrockPong advertisement = new BedrockPong();

    @Getter private final KeyPair keyPair = EncryptionUtils.createKeyPair();

    public BedrockInterface(Server server) {
        this.server = server;

        var bootstrap = new ServerBootstrap()
                .channelFactory(RakChannelFactory.server(NioDatagramChannel.class))
                .group(new NioEventLoopGroup())
                .childHandler(new ServerInitializer())
                .localAddress(server.getIp(), server.getBedrockPort());

        this.channels.add(bootstrap.bind()
                .awaitUninterruptibly()
                .channel());
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
        this.advertisement
                .edition("MCPE")
                .motd(name).subMotd(subName)
                .playerCount(this.server.getPlayerCount())
                .maximumPlayerCount(100)
                .version("1.20.41")
                .protocolVersion(622)
                .gameType("Survival")
                .nintendoLimited(false);
        var asBuffer = this.advertisement.toByteBuf();

        // Apply to all channels.
        this.channels.forEach(channel ->
                channel.config().setOption(
                        RakChannelOption.RAK_ADVERTISEMENT,
                        asBuffer));
    }

    @Override
    public void shutdown() {
        this.channels.forEach(channel ->
                channel.close().awaitUninterruptibly());
    }

    @Override
    public void emergencyShutdown() {
        this.shutdown();
    }

    private final class ServerInitializer extends BedrockServerInitializer {
        @Override
        protected void initSession(BedrockServerSession session) {
            var networkSession = BedrockNetworkSession.from(session);
            BedrockInterface.this.server.getSessions().add(networkSession);

            networkSession.setPacketHandler(new LoginPacketHandler(
                    session, networkSession, server, BedrockInterface.this));

            session.setLogging(true);
            session.setPacketHandler(networkSession);
            networkSession.getLogger().debug("Player is attempting to connect.");
        }
    }
}
