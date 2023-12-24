package moe.seikimo.droplet.network.bedrock.handlers;

import lombok.Getter;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
import moe.seikimo.droplet.player.Player;
import moe.seikimo.droplet.world.chunk.section.DropletChunkSection;
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

        switch (command) {
            case "/test" -> {
                System.out.println("test");
            }
            case "/world" -> {
                var world = Server.getInstance().getDefaultWorld();

                System.out.println(world.getName());
                System.out.println(world.getSeed());

                for (var chunk : world.getChunks().values()) {
                    for (var section : chunk.getSections()) {
                        if (section == null) continue;

                        if (section instanceof DropletChunkSection droplet) {
                            if (droplet.getPalette().isEmpty()) {
                                System.out.printf("%s is empty.%n", droplet.getY());
                                continue;
                            }

                            System.out.println(droplet.getPalette());
                            System.out.println(droplet.getBlockStates());
                        }
                    }
                }
            }
        }

        return PacketSignal.HANDLED;
    }
}
