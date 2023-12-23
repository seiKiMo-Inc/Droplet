package moe.seikimo.droplet.network.bedrock.handlers;

import lombok.Getter;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
import moe.seikimo.droplet.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

@Getter
public final class BedrockPlayerPacketHandler implements BedrockPacketHandler {
    private final BedrockNetworkSession session;
    private final Player player;

    public BedrockPlayerPacketHandler(BedrockNetworkSession session) {
        this.session = session;
        this.player = session.getPlayer();

        this.getSession().getLogger().debug("Switched to the game packet handler.");
    }

    @Override
    public PacketSignal handle(MovePlayerPacket packet) {
        // We do not handle this packet.
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TickSyncPacket packet) {
        // Unused packet. Used for logging.
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RequestChunkRadiusPacket packet) {
        this.getPlayer().setRenderDistance(packet.getRadius());
        return PacketSignal.HANDLED;
    }
}
