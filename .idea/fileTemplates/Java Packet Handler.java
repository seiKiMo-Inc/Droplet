package ${PACKAGE_NAME};

import com.github.steveice10.mc.protocol.packet.ingame.serverbound.Serverbound${NAME}Packet;
import com.github.steveice10.packetlib.packet.Packet;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.network.java.JavaPacketHandler;

public final class Java${NAME}Handler extends JavaPacketHandler<Serverbound${NAME}Packet> {
    @Override
    protected Class<? extends Packet> getPacketClass() {
        return Serverbound${NAME}Packet.class;
    }

    @Override
    protected void handle(NetworkSession session, Serverbound${NAME}Packet packet) {

    }
}
