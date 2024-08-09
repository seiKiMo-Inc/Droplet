package moe.seikimo.droplet.network.bedrock.handlers;

import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.entity.DropletEntity;
import moe.seikimo.droplet.inventory.InventoryViewer;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
import moe.seikimo.droplet.network.shared.play.DropletStartGamePacket;
import moe.seikimo.droplet.network.shared.play.DropletTimePacket;
import moe.seikimo.droplet.utils.FileUtils;
import moe.seikimo.droplet.utils.ThreadUtils;
import moe.seikimo.droplet.utils.enums.Dimension;
import moe.seikimo.droplet.world.biome.Biome;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.GameRuleData;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

import java.io.File;

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
        var player = this.networkSession.getPlayer();

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

        // Prepare the entity list packet.
        logger.debug("Sending entity identifiers.");
        var entitiesPacket = new AvailableEntityIdentifiersPacket();
        entitiesPacket.setIdentifiers(DropletEntity.getEntityIdentifiers());
        this.networkSession.sendPacket(entitiesPacket);

        // Prepare the biome definition packet.
        logger.debug("Sending biome definitions.");
        var biomePacket = new BiomeDefinitionListPacket();
        biomePacket.setDefinitions(Biome.getBiomeDefinitions());
        this.networkSession.sendPacket(biomePacket);

        // Prepare the creative content packet.
        logger.debug("Sending creative content.");
        var creativePacket = new CreativeContentPacket();
        creativePacket.setContents(this.server.getItemManager()
                .getCreativeItems().toArray(new ItemData[0]));
        this.networkSession.sendPacket(creativePacket);

        // DEBUG
        var gamerulePacket = new GameRulesChangedPacket();
        gamerulePacket.getGameRules().add(new GameRuleData<>(
                "showcoordinates", true));
        this.networkSession.sendPacket(gamerulePacket);
        // DEBUG
    }

    @Override
    public PacketSignal handle(RequestChunkRadiusPacket packet) {
        var player = this.networkSession.getPlayer();

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

        if (this.networkSession instanceof BedrockNetworkSession bdSession) {
            // Set player abilities.
            bdSession.syncAbilities();
            // Set player adventure settings.
            bdSession.syncAdventureSettings();
        }

        // Set player attributes.
        var attributesPacket = new UpdateAttributesPacket();
        attributesPacket.setRuntimeEntityId(player.getEntityId());
        attributesPacket.setTick(50000);
        this.networkSession.sendPacket(attributesPacket);

        // Begin to send chunks.
//        var world = Server.getInstance().getDefaultWorld();
//        for (var x = -2; x <= 2; x++) {
//            for (var z = -2; z <= 2; z++) {
//                var chunk = world.getChunkAt(x, z);
//                if (chunk == null) {
//                    System.out.printf("Chunk at %s, %s is null.%n", x, z);
//                    continue;
//                }
//
//                var chunkPacket = new DropletChunkPacket(chunk);
//                this.networkSession.sendPacket(chunkPacket);
//            }
//        }

        for (var x = 0; x < 21; x++) {
            for (var y = 0; y < 21; y++) {
                var file = new File("chunks/" + x + "." + y + "-serialized");
                if (!file.exists()) continue;

                var chunkPacket = FileUtils.readPacket(0x3a, file);
                this.networkSession.sendPacket(chunkPacket);
            }
        }

        // Respawn the player.
        var respawnPacket = new RespawnPacket();
        respawnPacket.setRuntimeEntityId(player.getEntityId());
        respawnPacket.setPosition(Vector3f.ZERO);
        respawnPacket.setState(RespawnPacket.State.SERVER_READY);
        this.networkSession.sendPacket(respawnPacket);

        ThreadUtils.runAfter(() -> {
            // Set entity data for player.
            var metadataPacket = new SetEntityDataPacket();
            metadataPacket.setRuntimeEntityId(player.getEntityId());
            metadataPacket.setTick(0);
            this.networkSession.sendPacket(metadataPacket);

            // Add player to the server list.
            this.server.addPlayerToList(this.networkSession.getPlayer());

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
            this.networkSession.sendPacketImmediately(statusPacket);

            // Set the world time.
            var timePacket = new DropletTimePacket(6000);
            this.networkSession.sendPacket(timePacket);

            // Switch packet handlers to the spawn handler.
            this.networkSession.setPacketHandler(new SpawnPacketHandler(
                    this.session, this.networkSession));
        }, 100);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerAuthInputPacket packet) {
        return PacketSignal.HANDLED;
    }
}
