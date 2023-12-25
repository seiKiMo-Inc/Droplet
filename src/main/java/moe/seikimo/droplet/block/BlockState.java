package moe.seikimo.droplet.block;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.nbt.NbtMap;

@Data
public final class BlockState {
    private final int bedrockRuntimeId;
    private final NbtMap javaState, bedrockState;
    private final String javaIdentifier, bedrockIdentifier;
}
