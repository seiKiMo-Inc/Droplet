package moe.seikimo.droplet.network.bedrock.handlers;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.entity.DropletEntity;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
import moe.seikimo.droplet.network.shared.play.DropletChunkPacket;
import moe.seikimo.droplet.network.shared.play.DropletStartGamePacket;
import moe.seikimo.droplet.player.DropletPlayer;
import moe.seikimo.droplet.utils.ThreadUtils;
import moe.seikimo.droplet.utils.enums.Dimension;
import moe.seikimo.droplet.utils.enums.Platform;
import moe.seikimo.droplet.world.biome.Biome;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.GameRuleData;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

// network chunk packet update thingy
// send 12 non-null chunks in a 4x4 grid
// send play status AFTER all is sent
public final class PreSpawnPacketHandler implements BedrockPacketHandler {
    private final Server server;

    private final BedrockServerSession session;
    private final BedrockNetworkSession networkSession;

    public PreSpawnPacketHandler(BedrockServerSession session, BedrockNetworkSession networkSession) {
        this.server = Server.getInstance();

        this.session = session;
        this.networkSession = networkSession;

        this.init();
    }

    private void init() {
        var logger = this.networkSession.getLogger();

        // Create a new player instance.
        // TODO: Fetch platform from client.
        var player = new DropletPlayer(
                DropletEntity.nextEntityId++,
                this.server.getDefaultWorld(),
                this.networkSession,
                Platform.WINDOWS);
        this.networkSession.setPlayer(player);
        logger.debug("Created player.");

        // Prepare the game start packet.
        logger.debug("Preparing StartGamePacket.");
        var startPacket = new DropletStartGamePacket(
                (int) player.getEntityId(), false,
                GameMode.CREATIVE, Dimension.OVERWORLD
        );
        // Set the definitions.
        session.getPeer().getCodecHelper().setItemDefinitions(
                Server.getInstance().getItemManager().getRegistry());
        session.getPeer().getCodecHelper()
                .setBlockDefinitions(BlockPalette.getBedrockRegistry());
        this.networkSession.sendPacket(startPacket);

        // Prepare the creative content packet.
        logger.debug("Sending creative content.");
        var creativePacket = new CreativeContentPacket();
        creativePacket.setContents(this.server.getItemManager()
                .getCreativeItems().toArray(new ItemData[0]));
        this.networkSession.sendPacket(creativePacket);

        // Prepare the biome definition packet.
        logger.debug("Sending biome definitions.");
        var biomePacket = new BiomeDefinitionListPacket();
        biomePacket.setDefinitions(Biome.getBiomeDefinitions());
        this.networkSession.sendPacket(biomePacket);

        // DEBUG
        var gamerulePacket = new GameRulesChangedPacket();
        gamerulePacket.getGameRules().add(new GameRuleData<>(
                "showcoordinates", true));
        this.networkSession.sendPacket(gamerulePacket);
        // DEBUG
    }

    @Override
    public PacketSignal handle(RequestChunkRadiusPacket packet) {
        this.networkSession.getLogger().debug("Setting player chunk radius to {}.",
                packet.getRadius());

        // Sync view radius.
        var radiusPacket = new ChunkRadiusUpdatedPacket();
        radiusPacket.setRadius(2);
        this.networkSession.sendPacket(radiusPacket);

        // Sync center point.
        var publisherPacket = new NetworkChunkPublisherUpdatePacket();
        publisherPacket.setPosition(Vector3i.ZERO);
        publisherPacket.setRadius(32);
        this.networkSession.sendPacketImmediately(publisherPacket);

        // Begin to send chunks.
        var world = Server.getInstance().getDefaultWorld();
        for (var x = -2; x <= 2; x++) {
            for (var z = -2; z <= 2; z++) {
                var chunk = world.getChunkAt(x, z);
                if (chunk == null) {
                    System.out.printf("Chunk at %s, %s is null.%n", x, z);
                    continue;
                }

                var chunkPacket = new DropletChunkPacket(chunk);
                this.networkSession.sendPacket(chunkPacket);
            }
        }

        // Respawn the player.
        var respawnPacket = new RespawnPacket();
        respawnPacket.setRuntimeEntityId(0);
        respawnPacket.setPosition(Vector3f.ZERO);
        respawnPacket.setState(RespawnPacket.State.SERVER_READY);
        this.networkSession.sendPacket(respawnPacket);

        ThreadUtils.runAfter(() -> {
            // Send the player's inventory.
            if (player instanceof InventoryViewer viewer) {
                viewer.sendContents(player.getInventory(), ContainerId.INVENTORY);

                // TODO: Reference alternate player inventories.
                viewer.sendContents(player.getInventory(), ContainerId.OFFHAND);
                viewer.sendContents(player.getInventory(), ContainerId.ARMOR);
            }
            // Prepare a response packet.
            var statusPacket = new PlayStatusPacket();
            statusPacket.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
            this.networkSession.sendPacket(statusPacket);

            // Switch packet handlers to the spawn handler.
            this.session.setPacketHandler(new SpawnPacketHandler(
                    this.session, this.networkSession));
        }, 5000);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerAuthInputPacket packet) {
        return PacketSignal.HANDLED;
    }
}
