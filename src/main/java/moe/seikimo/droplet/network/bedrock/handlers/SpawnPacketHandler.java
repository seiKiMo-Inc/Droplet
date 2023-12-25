package moe.seikimo.droplet.network.bedrock.handlers;

import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
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

        this.session.setPacketHandler(
                new PlayerPacketHandler(
                        this.networkSession));

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerAuthInputPacket packet) {
        return PacketSignal.HANDLED;
    }
}
