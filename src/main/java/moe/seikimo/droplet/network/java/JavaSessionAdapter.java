package moe.seikimo.droplet.network.java;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;

public final class JavaSessionAdapter extends SessionAdapter {
    @Override
    public void packetReceived(Session session, Packet packet) {
        JavaPacketHandler.processPacket(
                JavaNetworkSession.from(session), packet);
    }
}
