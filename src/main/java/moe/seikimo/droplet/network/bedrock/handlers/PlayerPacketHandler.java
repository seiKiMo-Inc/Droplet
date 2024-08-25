package moe.seikimo.droplet.network.bedrock.handlers;

import lombok.Getter;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
import moe.seikimo.droplet.player.Player;
import moe.seikimo.droplet.world.chunk.DropletChunkSection;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

import java.util.Arrays;

@Getter
public final class PlayerPacketHandler implements BedrockPacketHandler {
    private final BedrockNetworkSession session;
    private final Player player;

    public PlayerPacketHandler(BedrockNetworkSession session) {
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

        // Prepare network chunk publisher update.
        var publisherPacket = new NetworkChunkPublisherUpdatePacket();
        publisherPacket.setPosition(Vector3i.ZERO);
        publisherPacket.setRadius(32);
        this.getSession().sendPacket(publisherPacket);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerAuthInputPacket packet) {
        // TODO: Handle PlayerAuthInputPacket.
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetLocalPlayerAsInitializedPacket packet) {
        this.getPlayer().setSpawned(true);
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerSettingsRequestPacket packet) {
        // TODO: Handle ServerSettingsRequestPacket.
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TextPacket packet) {
        switch (packet.getType()) {
            case CHAT -> {
                this.getSession().getLogger().info("{}", packet.getMessage());
            }
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CommandRequestPacket packet) {
        var command = packet.getCommand();
        var sender = packet.getCommandOriginData();

        // TODO: Handle CommandRequestPacket.

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BlockPickRequestPacket packet) {
        var targetBlock = packet.getBlockPosition();
        var world = Server.getInstance().getDefaultWorld();
        var chunk = world.getChunkAt(targetBlock.getX(), targetBlock.getZ());
        var section = chunk.getSections()[targetBlock.getY() >> 4];
        System.out.println(
                section.getBlockAt(targetBlock.getX() % 16, targetBlock.getY() % 16, targetBlock.getZ() % 16)
        );
        return PacketSignal.HANDLED;
    }
}
