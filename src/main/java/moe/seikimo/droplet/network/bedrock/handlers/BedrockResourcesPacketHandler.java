package moe.seikimo.droplet.network.bedrock.handlers;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.entity.DropletEntity;
import moe.seikimo.droplet.network.ProtocolInfo;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
import moe.seikimo.droplet.network.shared.play.DropletChunkPacket;
import moe.seikimo.droplet.network.shared.play.DropletStartGamePacket;
import moe.seikimo.droplet.player.DropletPlayer;
import moe.seikimo.droplet.utils.enums.Dimension;
import moe.seikimo.droplet.utils.enums.Platform;
import moe.seikimo.droplet.world.biome.Biome;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket.Status;
import org.cloudburstmc.protocol.common.PacketSignal;

public final class BedrockResourcesPacketHandler implements BedrockPacketHandler {
    private final Server server;

    private final BedrockServerSession session;
    private final BedrockNetworkSession networkSession;

    public BedrockResourcesPacketHandler(BedrockServerSession session, BedrockNetworkSession networkSession) {
        this.server = Server.getInstance();
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

                // Create a new player instance.
                // TODO: Fetch platform from client.
                var player = new DropletPlayer(
                        DropletEntity.nextEntityId++,
                        this.server.getDefaultWorld(),
                        this.networkSession,
                        Platform.WINDOWS);
                this.networkSession.setPlayer(player);

                // Switch to the game packet handler.
                this.session.setPacketHandler(
                        new BedrockPlayerPacketHandler(
                                this.networkSession));

                // Prepare the game start packet.
                var startPacket = new DropletStartGamePacket(
                        0, false, GameMode.CREATIVE, Dimension.OVERWORLD
                );
                this.networkSession.sendPacket(startPacket);

                // Prepare the creative content packet.
                var creativePacket = new CreativeContentPacket();
                creativePacket.setContents(this.server.getItemManager()
                        .getCreativeItems().toArray(new ItemData[0]));
                this.networkSession.sendPacket(creativePacket);

                // Prepare the biome definition packet.
                var biomePacket = new BiomeDefinitionListPacket();
                biomePacket.setDefinitions(Biome.getBiomeDefinitions());
                this.networkSession.sendPacket(biomePacket);

                // Send all chunks to the player.
                for (var chunk : this.server.getDefaultWorld()
                        .getChunks().values()) {
                    var chunkPacket = new DropletChunkPacket(chunk);
                    this.networkSession.sendPacket(chunkPacket);
                }

                // Send play status packet.
                var statusPacket = new PlayStatusPacket();
                statusPacket.setStatus(Status.PLAYER_SPAWN);
                this.networkSession.sendPacket(statusPacket);

                yield PacketSignal.HANDLED;
            }
        };
    }
}
