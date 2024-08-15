package moe.seikimo.droplet.network.shared.play;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetContentPacket;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import moe.seikimo.droplet.inventory.Item;
import moe.seikimo.droplet.network.shared.BasePacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.InventoryContentPacket;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @see InventoryContentPacket For Bedrock clients.
 * @see <a href="https://github.com/Sandertv/gophertunnel/blob/master/minecraft/protocol/packet/inventory_content.go">...</a>
 * @see ClientboundContainerSetContentPacket For Java clients.
 * @see <a href="https://wiki.vg/Protocol#Set_Container_Content">...</a>
 */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public final class DropletSetInventoryContentPacket extends BasePacket {
    private final int containerId; // containerId; containerId
    private final List<Item> items; // contents; items

    private int stateId; // (no equivalent); stateId
    @Nullable private Item carriedItem; // carriedItem; carriedItem

    @Override
    public Collection<BedrockPacket> toBedrock() {
        var packet = new InventoryContentPacket();
        packet.setContainerId(this.containerId);
        packet.setContents(this.items.stream()
                .map(Item::toBedrock)
                .toList());

        return List.of(packet);
    }

    @Override
    public Collection<Packet> toJava() {
        return List.of(new ClientboundContainerSetContentPacket(
                this.containerId,
                this.stateId,
                this.items.stream()
                        .map(Item::toJava)
                        .toArray(ItemStack[]::new),
                this.carriedItem == null ? null : this.carriedItem.toJava()
        ));
    }
}
