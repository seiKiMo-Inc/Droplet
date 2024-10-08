package moe.seikimo.droplet.network.shared.play;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundLevelChunkWithLightPacket;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.world.chunk.Chunk;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelChunkPacket;

import java.util.Collection;
import java.util.List;

/**
 * @see LevelChunkPacket For Bedrock clients.
 * @see <a href="https://github.com/Sandertv/gophertunnel/blob/master/minecraft/protocol/packet/level_chunk.go">Bedrock Documentation</a>
 * @see ClientboundLevelChunkWithLightPacket For Java clients.
 * @see <a href="https://wiki.vg/Protocol#Chunk_Data_and_Update_Light">Java Documentation</a>
 */
@RequiredArgsConstructor
public final class DropletChunkPacket extends BasePacket {
    private final Chunk chunk;

    @Override
    public Collection<BedrockPacket> toBedrock() {
        var chunk = this.chunk;

        var packet = new LevelChunkPacket();
        packet.setChunkX(chunk.getX());
        packet.setChunkZ(chunk.getZ());
        // TODO: Support chunk caching.
        packet.setCachingEnabled(false);
        packet.setRequestSubChunks(false);
        packet.setSubChunksLength(chunk.getSections().length);
        packet.setData(chunk.encodeBedrock());

        return List.of(packet);
    }

    @Override
    public Collection<Packet> toJava() {
        var chunk = this.chunk;

        return null;
//        return new ClientboundLevelChunkWithLightPacket(
//                chunk.getX(), chunk.getZ(),
//                chunk.encodeJava(),
//                new CompoundTag("heightmap"),
//                new BlockEntityInfo[0],
//                new LightUpdateData()
//        );
    }
}
