package moe.seikimo.droplet.block;

import lombok.Data;
import org.cloudburstmc.nbt.NbtMap;

@Data
public final class BlockState {
    private final int javaRuntimeId, bedrockRuntimeId;
    private final NbtMap javaState, bedrockState;
    private final String javaIdentifier, bedrockIdentifier;
}
