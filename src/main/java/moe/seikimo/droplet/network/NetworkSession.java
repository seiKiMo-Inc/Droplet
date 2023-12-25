package moe.seikimo.droplet.network;

import com.github.steveice10.packetlib.packet.Packet;
import lombok.Getter;
import lombok.Setter;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.player.Player;
import moe.seikimo.droplet.utils.Log;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.slf4j.Logger;

public abstract class NetworkSession {
    private static int nextId = 1;

    @Getter private final Logger logger
            = Log.newLogger("Network Session #" + nextId++);
    @Setter private Player player;

    public NetworkSession() {
        Log.setDebug(this.logger);
    }

    /**
     * Sends a packet to the client.
     *
     * @param packet The packet to send.
     */
    public abstract void sendPacket(BasePacket packet);

    /**
     * Sends one or more packets to the client.
     *
     * @param packets The packets to send.
     */
    public void sendPacket(BedrockPacket... packets) {
        throw new UnsupportedOperationException("Cannot send Bedrock packets to Java clients.");
    }

    /**
     * Sends one or more packets to the client.
     *
     * @param packets The packets to send.
     */
    public void sendPacket(Packet... packets) {
        throw new UnsupportedOperationException("Cannot send Java packets to Bedrock clients.");
    }

    /**
     * Sends one or more packets to the client.
     *
     * @param packets The packets to send.
     */
    public void sendPacketImmediately(BedrockPacket... packets) {
        throw new UnsupportedOperationException("Cannot send Bedrock packets to Java clients.");
    }

    /**
     * Sends one or more packets to the client.
     *
     * @param packets The packets to send.
     */
    public void sendPacketImmediately(Packet... packets) {
        throw new UnsupportedOperationException("Cannot send Java packets to Bedrock clients.");
    }
}
