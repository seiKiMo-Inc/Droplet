package moe.seikimo.droplet.player;

import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.utils.enums.Platform;
import moe.seikimo.droplet.world.World;

public interface Player {
    /**
     * @return The player's network handle.
     */
    NetworkSession getNetworkSession();

    /**
     * @return The player's connecting platform.
     */
    Platform getPlatform();

    /**
     * @return The world the player is in.
     */
    World getWorld();
}
