package moe.seikimo.droplet.network.java.handlers;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundKeyPacket;
import moe.seikimo.droplet.network.java.JavaNetworkSession;
import moe.seikimo.droplet.network.shared.play.DropletStartGamePacket;
import moe.seikimo.droplet.utils.enums.Dimension;

public interface JavaLoginPacketHandler {
    /**
     * Handles the {@link ServerboundKeyPacket}.
     *
     * @param session The session.
     * @param packet The packet.
     */
    static void handle(JavaNetworkSession session, ServerboundKeyPacket packet) {
        session.sendPacket(new DropletStartGamePacket(
                0, false, GameMode.CREATIVE, Dimension.OVERWORLD
        ));
    }
}
