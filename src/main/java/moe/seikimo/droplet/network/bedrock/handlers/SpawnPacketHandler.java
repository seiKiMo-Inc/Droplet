package moe.seikimo.droplet.network.bedrock.handlers;

import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
import moe.seikimo.droplet.network.shared.play.DropletMoveEntityAbsolutePacket;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

@RequiredArgsConstructor
public final class SpawnPacketHandler implements BedrockPacketHandler {
    private final BedrockServerSession session;
    private final BedrockNetworkSession networkSession;

    @Override
    public PacketSignal handle(SetLocalPlayerAsInitializedPacket packet) {
        this.networkSession.getLogger().debug("Received spawn response, entering in-game phase.");

        var player = this.networkSession.getPlayer();

        // Send actor metadata
        var metadataPacket = new SetEntityDataPacket();
        metadataPacket.setRuntimeEntityId(player.getEntityId());
        metadataPacket.setTick(0);
        player.getData().restore(
                metadataPacket.getMetadata(),
                metadataPacket.getProperties());
        this.networkSession.sendPacket(metadataPacket);

        this.networkSession.setPacketHandler(
                new PlayerPacketHandler(
                        this.networkSession));

        // TODO: Fetch player's last position.
        // this.networkSession.sendPacket(new DropletMoveEntityAbsolutePacket(
        //         this.networkSession.getPlayer().getEntityId(),
        //         Vector3f.from(0, 80, 0),
        //         Vector3f.ZERO,
        //         true
        // ));

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerAuthInputPacket packet) {
        return PacketSignal.HANDLED;
    }
}
