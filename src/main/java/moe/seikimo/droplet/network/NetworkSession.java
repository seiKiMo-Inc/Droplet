package moe.seikimo.droplet.network;

import lombok.Getter;
import lombok.Setter;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.player.Player;
import moe.seikimo.droplet.utils.Log;
import org.slf4j.Logger;

public abstract class NetworkSession {
    private static int nextId = 0;

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
}
