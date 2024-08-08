package moe.seikimo.droplet.player;

import moe.seikimo.droplet.entity.LivingEntity;
import moe.seikimo.droplet.inventory.Inventory;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.player.data.DeviceInfo;
import moe.seikimo.droplet.utils.enums.Device;
import moe.seikimo.droplet.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Player extends LivingEntity {
    /**
     * @return The player's network handle.
     */
    NetworkSession getNetworkSession();

    /**
     * @return The player's connecting platform.
     */
    Device getPlatform();

    /**
     * @return The player's device information.
     */
    @Nullable DeviceInfo getDeviceInfo();

    /**
     * @return The world the player is in.
     */
    World getWorld();

    /**
     * @return The player's unique identifier.
     */
    UUID getUuid();

    /**
     * @return The player's unique Xbox Live identifier.
     */
    @Nullable String getXuid();

    /**
     * Sets the player's skin.
     *
     * @param skin The new skin.
     */
    void setSkin(DropletSkin skin);

    /**
     * @return The player's skin.
     */
    DropletSkin getSkin();

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

    /**
     * @return The player's display name.
     */
    String getDisplayName();

    /**
     * @return The player's primary inventory.
     */
    Inventory getInventory();
}
