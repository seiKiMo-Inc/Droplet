package moe.seikimo.droplet.network.java.handlers;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.packet.configuration.serverbound.ServerboundFinishConfigurationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.java.JavaNetworkSession;
import moe.seikimo.droplet.network.shared.play.DropletChunkPacket;
import moe.seikimo.droplet.network.shared.play.DropletStartGamePacket;
import moe.seikimo.droplet.utils.ThreadUtils;
import moe.seikimo.droplet.utils.enums.Dimension;

public interface LoginPacketHandler {
    /**
     * Handles the {@link ServerboundFinishConfigurationPacket}.
     *
     * @param session The session.
     */
    static void handle(JavaNetworkSession session) {
        session.sendPacket(new DropletStartGamePacket(
                0, false, GameMode.CREATIVE, Dimension.OVERWORLD
        ));

        var i = 0;
        for (var chunk : Server.getInstance().getDefaultWorld().getChunks().values()) {
            if (i++ > 12) break;

            if (chunk != null) session.sendPacket(new DropletChunkPacket(chunk));
        }

        ThreadUtils.runAfter(() -> {
            session.sendPacket(new ClientboundPlayerPositionPacket(
                    0, 0, 0, 0, 0, 0
            ));
        }, 3000);
    }
}
