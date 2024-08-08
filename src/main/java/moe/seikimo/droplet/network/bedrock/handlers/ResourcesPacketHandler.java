package moe.seikimo.droplet.network.bedrock.handlers;

import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.ProtocolInfo;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
import moe.seikimo.droplet.network.shared.play.DropletChunkPacket;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

public final class ResourcesPacketHandler implements BedrockPacketHandler {
    private final BedrockServerSession session;
    private final BedrockNetworkSession networkSession;

    public ResourcesPacketHandler(BedrockServerSession session, BedrockNetworkSession networkSession) {
        this.session = session;
        this.networkSession = networkSession;

        this.networkSession.getLogger().debug("Switched to resource packs packet handler.");
    }

    @Override
    public PacketSignal handle(ResourcePackClientResponsePacket packet) {
        return switch (packet.getStatus()) {
            default -> PacketSignal.UNHANDLED;
            case HAVE_ALL_PACKS -> {
                // TODO: Replace with proper resource pack implementation.
                var stackPacket = new ResourcePackStackPacket();
                stackPacket.setForcedToAccept(true);
                stackPacket.setExperimentsPreviouslyToggled(true);
                stackPacket.setGameVersion(ProtocolInfo.BEDROCK_CODEC.getMinecraftVersion());
                this.networkSession.sendPacket(stackPacket);

                yield PacketSignal.HANDLED;
            }
            case COMPLETED -> {
                this.networkSession.getLogger().debug("Client has completed resource pack loading.");

                this.networkSession.setPacketHandler(new PreSpawnPacketHandler(
                        this.session, this.networkSession));

                yield PacketSignal.HANDLED;
            }
        };
    }
}
