package moe.seikimo.droplet.network.java;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.utils.enums.Device;
import moe.seikimo.handler.DataReceiver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
public final class JavaNetworkSession extends NetworkSession implements DataReceiver {
    private static final Map<Session, JavaNetworkSession> sessions
            = new ConcurrentHashMap<>();

    /**
     * Get the {@link JavaNetworkSession} for the given {@link Session}.
     *
     * @param session The session.
     * @return The network session.
     */
    public static JavaNetworkSession from(Session session) {
        return sessions.computeIfAbsent(session, k -> {
            var networkSession = new JavaNetworkSession(k);
            networkSession.setDevice(Device.JAVA);
            return networkSession;
        });
    }

    private final Session handle;

    @Override
    public void sendPacket(BasePacket packet) {
        packet.toJava().forEach(this::sendPacket);
    }

    @Override
    public void sendPacket(Packet... packets) {
        for (var packet : packets) {
            this.getHandle().send(packet);

            if (Server.getInstance().isLogPackets()) {
                this.getLogger().debug("Sent packet: {}", packet.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void sendPacketImmediately(Packet... packets) {
        this.sendPacket(packets);
    }
}
