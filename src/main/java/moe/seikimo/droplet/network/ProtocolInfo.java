package moe.seikimo.droplet.network;

import org.cloudburstmc.protocol.bedrock.codec.v712.Bedrock_v712;
import org.geysermc.mcprotocollib.protocol.codec.PacketCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftCodec;

public interface ProtocolInfo {
    BedrockCodec BEDROCK_CODEC = Bedrock_v712.CODEC;
    PacketCodec JAVA_CODEC = MinecraftCodec.CODEC;

    int BEDROCK_PROTOCOL = ProtocolInfo.BEDROCK_CODEC.getProtocolVersion();
    int JAVA_PROTOCOL = ProtocolInfo.JAVA_CODEC.getProtocolVersion();
}
