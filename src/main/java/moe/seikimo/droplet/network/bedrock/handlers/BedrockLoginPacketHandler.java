package moe.seikimo.droplet.network.bedrock.handlers;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.entity.DropletEntity;
import moe.seikimo.droplet.network.ProtocolInfo;
import moe.seikimo.droplet.network.bedrock.BedrockInterface;
import moe.seikimo.droplet.network.bedrock.BedrockNetworkSession;
import moe.seikimo.droplet.network.shared.play.DropletChunkPacket;
import moe.seikimo.droplet.network.shared.play.DropletStartGamePacket;
import moe.seikimo.droplet.player.DropletPlayer;
import moe.seikimo.droplet.utils.enums.Dimension;
import moe.seikimo.droplet.utils.enums.Platform;
import moe.seikimo.droplet.world.biome.Biome;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket.Status;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.jose4j.lang.JoseException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RequiredArgsConstructor
public final class BedrockLoginPacketHandler implements BedrockPacketHandler {
    private final BedrockServerSession session;
    private final BedrockNetworkSession networkSession;

    private final Server server;
    private final BedrockInterface netInterface;

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        var protocol = packet.getProtocolVersion();

        // Check if the protocols match.
        var serverProtocol = ProtocolInfo.BEDROCK_PROTOCOL;
        var statusPacket = new PlayStatusPacket();
        if (protocol > serverProtocol) {
            statusPacket.setStatus(Status.LOGIN_FAILED_SERVER_OLD);
        } else if (protocol < serverProtocol) {
            statusPacket.setStatus(Status.LOGIN_FAILED_CLIENT_OLD);
        }

        // Send the status packet if it has been set.
        if (statusPacket.getStatus() != null) {
            this.session.sendPacketImmediately(statusPacket);
            return PacketSignal.HANDLED;
        }

        // Set Bedrock codec version.
        this.session.setCodec(ProtocolInfo.BEDROCK_CODEC);

        // Establish compression settings.
        var settingsPacket = new NetworkSettingsPacket();
        settingsPacket.setCompressionThreshold(1);
        settingsPacket.setCompressionAlgorithm(PacketCompressionAlgorithm.ZLIB);
        this.session.sendPacketImmediately(settingsPacket);

        // Apply compression.
        this.session.setCompression(PacketCompressionAlgorithm.ZLIB);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LoginPacket packet) {
        try {
            // Parse the client's identity data.
            var chain = packet.getChain();
            var chainData = EncryptionUtils.validateChain(chain);
            var claims = chainData.identityClaims();
            var clientPublicKey = claims.parsedIdentityPublicKey();
            // System.out.println(claims.extraData);

            // Parse the client's data.
            var clientStr = packet.getExtra();
            var clientData = Jwts.parser()
                    .verifyWith(clientPublicKey)
                    .build()
                    .parseSignedClaims(clientStr);
            // System.out.println(clientData.getPayload());

            // Generate the JWT for the handshake.
            var keyPair = this.netInterface.getKeyPair();
            var salt = EncryptionUtils.generateRandomToken();
            var handshake = EncryptionUtils.createHandshakeJwt(keyPair, salt);

            // Create & send the handshake packet.
            var handshakePacket = new ServerToClientHandshakePacket();
            handshakePacket.setJwt(handshake);
            this.session.sendPacketImmediately(handshakePacket);

            // Enable encryption locally.
            var secretKey = EncryptionUtils.getSecretKey(
                    keyPair.getPrivate(), clientPublicKey, salt);
            this.session.enableEncryption(secretKey);

            // Create a new player instance.
            // TODO: Fetch platform from client.
            var player = new DropletPlayer(
                    DropletEntity.nextEntityId++,
                    this.server.getDefaultWorld(),
                    this.networkSession,
                    Platform.WINDOWS);
            this.networkSession.setPlayer(player);
            this.networkSession.setPlayer(player);
        } catch (JoseException | NoSuchAlgorithmException | InvalidKeySpecException exception) {
            this.server.getLogger().warn("Failed to parse JWT chain data.", exception);
            this.session.close("Invalid JWT chain data.");
            return PacketSignal.HANDLED;
        } catch (InvalidKeyException exception) {
            this.server.getLogger().warn("Unable to generate the encryption secret key.", exception);
            this.session.close("Unable to enable encryption.");
            return PacketSignal.HANDLED;
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientToServerHandshakePacket packet) {
        // Prepare a response packet.
        var statusPacket = new PlayStatusPacket();
        statusPacket.setStatus(Status.LOGIN_SUCCESS);
        this.session.sendPacketImmediately(statusPacket);

        // Prepare the resource info packet.
        var resourcesPacket = new ResourcePacksInfoPacket();
        resourcesPacket.setForcedToAccept(true);
        resourcesPacket.setForcingServerPacksEnabled(true);
        this.session.sendPacketImmediately(resourcesPacket);

        // TODO: Replace with proper resource pack implementation.
        var stackPacket = new ResourcePackStackPacket();
        stackPacket.setForcedToAccept(true);
        stackPacket.setExperimentsPreviouslyToggled(true);
        stackPacket.setGameVersion(ProtocolInfo.BEDROCK_CODEC.getMinecraftVersion());
        this.session.sendPacketImmediately(stackPacket);

        // Prepare the game start packet.
        var startPacket = new DropletStartGamePacket(
                0, false, GameMode.CREATIVE, Dimension.OVERWORLD
        );
        this.networkSession.sendPacket(startPacket);

        // Prepare the creative content packet.
        var creativePacket = new CreativeContentPacket();
        creativePacket.setContents(this.server.getItemManager()
                .getCreativeItems().toArray(new ItemData[0]));
        this.session.sendPacketImmediately(creativePacket);

        // Prepare the biome definition packet.
        var biomePacket = new BiomeDefinitionListPacket();
        biomePacket.setDefinitions(Biome.getBiomeDefinitions());
        this.session.sendPacketImmediately(biomePacket);

        // Prepare the level chunk packet.
        var chunkPacket = new DropletChunkPacket(
                this.server.getDefaultWorld().getChunkAt(0, 0));
        this.networkSession.sendPacket(chunkPacket);

        // Send play status packet.
        statusPacket.setStatus(Status.PLAYER_SPAWN);
        this.session.sendPacketImmediately(statusPacket);

        // Set the packet handler.
        this.session.setPacketHandler(
                new BedrockPlayerPacketHandler(
                        this.networkSession));

        return PacketSignal.HANDLED;
    }
}
