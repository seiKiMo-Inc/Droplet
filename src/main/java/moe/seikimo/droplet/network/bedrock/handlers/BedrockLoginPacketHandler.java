package moe.seikimo.droplet.network.bedrock.handlers;

import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.ProtocolInfo;
import moe.seikimo.droplet.network.bedrock.BedrockInterface;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket.Status;
import org.cloudburstmc.protocol.common.PacketSignal;

@RequiredArgsConstructor
public final class BedrockLoginPacketHandler implements BedrockPacketHandler {
    private final BedrockServerSession session;
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
        return PacketSignal.UNHANDLED;
    }
}
