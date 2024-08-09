package moe.seikimo.droplet.network.shared.play;

import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetSlotPacket;
import org.geysermc.mcprotocollib.network.packet.Packet;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import moe.seikimo.droplet.inventory.Item;
import moe.seikimo.droplet.network.shared.BasePacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;

import java.util.Collection;
import java.util.List;

/**
 * @see InventorySlotPacket For Bedrock clients.
 * @see <a href="https://github.com/Sandertv/gophertunnel/blob/master/minecraft/protocol/packet/inventory_slot.go">...</a>
 * @see ClientboundContainerSetSlotPacket For Java clients.
 * @see <a href="https://wiki.vg/Protocol#Set_Container_Slot">...</a>
 */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public final class DropletSetItemPacket extends BasePacket {
    private final int containerId; // containerId; containerId
    private final int slot; // slot; slot
    private final Item item; // item; item

    private int stateId; // (no equivalent); stateId

    @Override
    public Collection<BedrockPacket> toBedrock() {
        var packet = new InventorySlotPacket();
        packet.setContainerId(this.containerId);
        packet.setSlot(this.slot);
        packet.setItem(this.item.toBedrock());

        return List.of(packet);
    }

    @Override
    public Collection<Packet> toJava() {
        return List.of(new ClientboundContainerSetSlotPacket(
                this.containerId,
                this.stateId,
                this.slot,
                this.item.toJava()
        ));
    }
}
