package moe.seikimo.droplet.inventory;

public interface InventoryViewer {
    /**
     * Sends the contents of the inventory to the viewer.
     *
     * @param inventory The inventory to send.
     * @param container The ID of the inventory.
     */
    void sendContents(Inventory inventory, int container);
}
