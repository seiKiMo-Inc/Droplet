package moe.seikimo.droplet.network;

import org.geysermc.mcprotocollib.network.packet.Packet;
import lombok.Getter;
import lombok.Setter;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.player.Player;
import moe.seikimo.droplet.utils.Log;
import moe.seikimo.droplet.utils.enums.Device;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.slf4j.Logger;

public abstract class NetworkSession {
    private static int nextId = 1;

    @Getter private final Logger logger
            = Log.newLogger(STR."Network Session #\{nextId++}");

    @Getter @Setter
    private Device device;

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
