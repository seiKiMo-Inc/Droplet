package moe.seikimo.droplet.inventory;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

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
