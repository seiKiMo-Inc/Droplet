package moe.seikimo.droplet.network.java.handlers;

import moe.seikimo.droplet.network.java.JavaNetworkSession;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundCustomPayloadPacket;

public interface CommonPacketHandler {
    /**
     * Handles the {@link ServerboundCustomPayloadPacket}.
     *
     * @see <a href="https://wiki.vg/Protocol#Serverbound_Plugin_Message_.28configuration.29">...</a>
     */
    static void handle(JavaNetworkSession session, ServerboundCustomPayloadPacket packet) {
        var channel = packet.getChannel().asString();
        switch (channel) {
            default -> session.getLogger().debug("Received unhandled plugin message: {}", channel);
        }
    }
}
