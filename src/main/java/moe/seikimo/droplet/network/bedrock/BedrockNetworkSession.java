package moe.seikimo.droplet.network.bedrock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.player.Player;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;

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
        packet.toBedrock().forEach(this.getHandle()::sendPacket);
    }
}
