package moe.seikimo.droplet.inventory;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;

public interface Item {
    /**
     * @return A Bedrock item stack.
     */
    ItemData toBedrock();

    /**
     * @return A Java item stack.
     */
    ItemStack toJava();
}
