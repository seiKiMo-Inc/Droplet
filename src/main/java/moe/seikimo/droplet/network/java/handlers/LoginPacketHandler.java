package moe.seikimo.droplet.network.java.handlers;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.level.notify.GameEvent;
import com.github.steveice10.mc.protocol.data.game.level.notify.GameEventValue;
import com.github.steveice10.mc.protocol.packet.configuration.serverbound.ServerboundFinishConfigurationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundGameEventPacket;
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

        session.sendPacket(new ClientboundGameEventPacket(
                GameEvent.LEVEL_CHUNKS_LOAD_START, null
        ));

        var world = Server.getInstance().getDefaultWorld();
        for (var x = -2; x <= 2; x++) {
            for (var z = -2; z <= 2; z++) {
                var chunk = world.getChunkAt(x, z);
                if (chunk != null) {
                    session.sendPacket(new DropletChunkPacket(chunk));
                } else {
                    session.getLogger().warn("Chunk ({}, {}) is null.", x, z);
                }
            }
        }

        ThreadUtils.runAfter(() -> {
            session.sendPacket(new ClientboundPlayerPositionPacket(
                    0, 0, 0, 0, 0, 0
            ));
        }, 3000);
    }
}
