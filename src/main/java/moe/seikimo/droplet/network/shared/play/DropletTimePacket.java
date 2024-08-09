package moe.seikimo.droplet.network.shared.play;

import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundSetTimePacket;
import org.geysermc.mcprotocollib.network.packet.Packet;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import moe.seikimo.droplet.network.shared.BasePacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetTimePacket;

import java.util.Collection;
import java.util.List;

/**
 * @see SetTimePacket For Bedrock clients.
 * @see <a href="https://github.com/Sandertv/gophertunnel/blob/master/minecraft/protocol/packet/set_time.go">...</a>
 * @see ClientboundSetTimePacket For Java clients.
 * @see <a href="https://wiki.vg/Protocol#Update_Time">...</a>
 */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public final class DropletTimePacket extends BasePacket {
    private final int worldTime; // time; time

    private long worldAge; // (no equivalent); worldAge

    @Override
    public Collection<BedrockPacket> toBedrock() {
        var packet = new SetTimePacket();
        packet.setTime(this.worldTime);

        return List.of(packet);
    }

    @Override
    public Collection<Packet> toJava() {
        return List.of(new ClientboundSetTimePacket(
                this.worldAge, this.worldTime));
    }
}
