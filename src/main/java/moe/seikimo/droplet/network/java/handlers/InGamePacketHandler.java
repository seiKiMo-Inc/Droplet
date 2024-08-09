package moe.seikimo.droplet.network.java.handlers;

import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import moe.seikimo.droplet.network.java.JavaNetworkSession;

public interface InGamePacketHandler {
    /**
     * Handles the {@link ServerboundChatCommandPacket}.
     *
     * @param session The session.
     * @param packet The packet.
     */
    static void handle(JavaNetworkSession session, ServerboundChatCommandPacket packet) {
        if (packet.getCommand().contains("world")) {
            session.sendPacket(new ClientboundPlayerPositionPacket(
                    0, 0, 0, 0, 0, 1
            ));
        }
    }
}
