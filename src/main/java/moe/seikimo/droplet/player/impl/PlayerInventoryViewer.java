package moe.seikimo.droplet.player.impl;

import moe.seikimo.droplet.inventory.Inventory;
import moe.seikimo.droplet.inventory.InventoryViewer;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.network.shared.play.DropletSetInventoryContentPacket;

public interface PlayerInventoryViewer extends InventoryViewer {
    /**
     * @return The player's network session.
     */
    NetworkSession getNetworkSession();

    /**
     * @see InventoryViewer#sendContents(Inventory, int)
     */
    default void sendContents(Inventory inventory, int container) {
        var packet = new DropletSetInventoryContentPacket(
                container, inventory.getContents());
        this.getNetworkSession().sendPacket(packet);
    }
}
