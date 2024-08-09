package moe.seikimo.droplet.network.shared;

import org.geysermc.mcprotocollib.network.packet.Packet;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.util.Collection;

public abstract class BasePacket {
    /**
     * @return Creates a Bedrock packet equivalent to this packet.
     */
    public abstract Collection<BedrockPacket> toBedrock();

    /**
     * @return Creates a Java packet equivalent to this packet.
     */
    public abstract Collection<Packet> toJava();
}
