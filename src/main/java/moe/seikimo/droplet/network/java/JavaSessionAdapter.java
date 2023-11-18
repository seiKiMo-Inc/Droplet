package moe.seikimo.droplet.network.java;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class JavaSessionAdapter extends SessionAdapter {
    private final JavaInterface netInterface;

    @Override
    public void packetReceived(Session session, Packet packet) {
        var sessionWrapper = JavaNetworkSession.from(session);
        this.netInterface.getPacketHandler().handle(sessionWrapper, packet);

        var logger = this.netInterface.getServer().getLogger();
        logger.info("Packet received from {}: {}",
                session.getRemoteAddress().toString(),
                packet.getClass().getSimpleName());
    }
}
