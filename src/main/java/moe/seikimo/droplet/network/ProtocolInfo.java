package moe.seikimo.droplet.network;

import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.mc.protocol.codec.PacketCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v594.Bedrock_v594;

public interface ProtocolInfo {
    BedrockCodec BEDROCK_CODEC = Bedrock_v594.CODEC;
    PacketCodec JAVA_CODEC = MinecraftCodec.CODEC;
}
