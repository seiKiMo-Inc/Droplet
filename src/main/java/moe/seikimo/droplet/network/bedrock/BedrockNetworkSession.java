package moe.seikimo.droplet.network.bedrock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.player.Player;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
public final class BedrockNetworkSession extends NetworkSession {
    private static final Map<BedrockServerSession, BedrockNetworkSession> sessions
            = new ConcurrentHashMap<>();

    /**
     * Get the {@link BedrockNetworkSession} for the given {@link BedrockServerSession}.
     *
     * @param session The session.
     * @return The network session.
     */
    public static BedrockNetworkSession from(BedrockServerSession session) {
        return sessions.computeIfAbsent(session, BedrockNetworkSession::new);
    }

    private final BedrockServerSession handle;

    @Setter private Player player;

    @Override
    public void sendPacket(BasePacket packet) {
        packet.toBedrock().forEach(this::sendPacket);
    }

    @Override
    public void sendPacket(BedrockPacket... packets) {
        for (var packet : packets) {
            this.getHandle().sendPacket(packet);

            if (Server.getInstance().isLogPackets()) {
                this.getLogger().debug("Sent packet: {}", packet.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void sendPacketImmediately(BedrockPacket... packets) {
        for (var packet : packets) {
            this.getHandle().sendPacketImmediately(packet);

            if (Server.getInstance().isLogPackets()) {
                this.getLogger().debug("Sent packet: {}", packet.getClass().getSimpleName());
            }
        }
    }
}
