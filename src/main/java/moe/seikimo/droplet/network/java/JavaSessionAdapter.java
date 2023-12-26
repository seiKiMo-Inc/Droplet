package moe.seikimo.droplet.network.java;

import com.github.steveice10.mc.protocol.packet.common.serverbound.ServerboundKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosRotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerRotPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.Server;

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
