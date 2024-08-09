package moe.seikimo.droplet.network.java;

import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.ConnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundKeepAlivePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosRotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerRotPacket;
import org.geysermc.mcprotocollib.network.packet.Packet;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public final class JavaSessionAdapter extends SessionAdapter {
    private static final Set<Class<? extends Packet>> BLACKLISTED = Set.of(
            ServerboundKeepAlivePacket.class,
            ServerboundMovePlayerRotPacket.class,
            ServerboundMovePlayerPosPacket.class,
            ServerboundMovePlayerPosRotPacket.class
    );

    private final JavaInterface netInterface;

    @Override
    public void connected(ConnectedEvent event) {
        var sessionWrapper = JavaNetworkSession.from(event.getSession());
        this.netInterface.getServer().getSessions().add(sessionWrapper);
    }

    @Override
    public void packetReceived(Session session, Packet packet) {
        var sessionWrapper = JavaNetworkSession.from(session);
        this.netInterface.getPacketHandler().handle(sessionWrapper, packet);

        if (!BLACKLISTED.contains(packet.getClass())) {
            sessionWrapper.getLogger().debug("Packet received from {}: {}",
                    session.getRemoteAddress().toString(),
                    packet.getClass().getSimpleName());
        }
    }
}
