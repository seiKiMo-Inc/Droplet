package moe.seikimo.droplet.network;

import org.geysermc.mcprotocollib.protocol.codec.PacketCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v686.Bedrock_v686;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftCodec;

public interface ProtocolInfo {
    BedrockCodec BEDROCK_CODEC = Bedrock_v686.CODEC;
    PacketCodec JAVA_CODEC = MinecraftCodec.CODEC;

    int BEDROCK_PROTOCOL = ProtocolInfo.BEDROCK_CODEC.getProtocolVersion();
    int JAVA_PROTOCOL = ProtocolInfo.JAVA_CODEC.getProtocolVersion();
}
