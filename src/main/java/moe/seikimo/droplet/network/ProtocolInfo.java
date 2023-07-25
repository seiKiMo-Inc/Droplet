package moe.seikimo.droplet.network;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v594.Bedrock_v594;

public interface ProtocolInfo {
    BedrockCodec BEDROCK_CODEC = Bedrock_v594.CODEC;
}
