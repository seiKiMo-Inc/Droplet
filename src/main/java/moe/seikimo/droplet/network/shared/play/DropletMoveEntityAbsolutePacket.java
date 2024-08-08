package moe.seikimo.droplet.network.shared.play;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosRotPacket;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import moe.seikimo.droplet.network.shared.BasePacket;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket;

import java.util.Collection;
import java.util.List;

/**
 * @see MoveEntityAbsolutePacket For Bedrock clients.
 * @see <a href="https://github.com/Sandertv/gophertunnel/blob/master/minecraft/protocol/packet/move_actor_absolute.go">...</a>
 * @see ClientboundMoveEntityPosRotPacket For Java clients.
 * @see <a href="https://wiki.vg/Protocol#Update_Entity_Position">...</a>
 */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public final class DropletMoveEntityAbsolutePacket extends BasePacket {
    private final long entityId; // runtimeEntityId; entityId
    private final Vector3f position; // position; moveX, moveY, moveZ
    private final Vector3f rotation; // rotation; yaw, pitch
    private final boolean onGround;

    private boolean teleport; // teleported; (no equivalent)
    private boolean force; // forceMove; (no equivalent)

    @Override
    public Collection<BedrockPacket> toBedrock() {
        var packet = new MoveEntityAbsolutePacket();
        packet.setRuntimeEntityId(this.entityId);
        packet.setPosition(this.position);
        packet.setRotation(this.rotation);
        packet.setOnGround(this.onGround);
        packet.setTeleported(this.teleport);
        packet.setForceMove(this.force);

        return List.of(packet);
    }

    @Override
    public Collection<Packet> toJava() {
        var position = this.position;
        var rotation = this.rotation;

        return List.of(new ClientboundMoveEntityPosRotPacket(
                (int) this.entityId,
                position.getX(), position.getY(), position.getZ(),
                rotation.getY(), rotation.getX(),
                this.onGround
        ));
    }
}
