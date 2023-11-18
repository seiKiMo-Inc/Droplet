package moe.seikimo.droplet.network;

import moe.seikimo.droplet.network.shared.BasePacket;

public abstract class NetworkSession {
    /**
     * Sends a packet to the client.
     *
     * @param packet The packet to send.
     */
    public abstract void sendPacket(BasePacket packet);
}
