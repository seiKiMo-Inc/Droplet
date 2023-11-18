package moe.seikimo.droplet.network.shared;

import com.github.steveice10.packetlib.packet.Packet;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public abstract class BasePacket {
    /**
     * @return Creates a Bedrock packet equivalent to this packet.
     */
    public abstract BedrockPacket toBedrock();

    /**
     * @return Creates a Java packet equivalent to this packet.
     */
    public abstract Packet toJava();
}
