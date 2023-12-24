package moe.seikimo.droplet.player;

import moe.seikimo.droplet.entity.LivingEntity;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.utils.enums.Platform;
import moe.seikimo.droplet.world.World;

public interface Player extends LivingEntity {
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

    /**
     * @return Whether the player is spawned.
     */
    boolean isSpawned();

    /**
     * Sets whether the player is spawned.
     *
     * @param spawned Whether the player is spawned.
     */
    void setSpawned(boolean spawned);

    /**
     * @return The player's render distance.
     */
    int getRenderDistance();

    /**
     * Sets the player's render distance.
     *
     * @param renderDistance The new render distance.
     */
    void setRenderDistance(int renderDistance);
}
