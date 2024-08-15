package moe.seikimo.droplet.inventory;

import java.util.List;

public interface Inventory {
    /**
     * @return The ordered inventory's contents by slot ID.
     */
    List<Item> getContents();
}
