package moe.seikimo.droplet.network.java.handlers;

import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundPlayerActionPacket;
import com.github.steveice10.packetlib.packet.Packet;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.network.java.JavaPacketHandler;

public final class JavaPlayerActionHandler extends JavaPacketHandler<ServerboundPlayerActionPacket> {
    @Override
    protected Class<? extends Packet> getPacketClass() {
        return ServerboundPlayerActionPacket.class;
    }

    @Override
    protected void handle(NetworkSession session, ServerboundPlayerActionPacket packet) {

    }
}
