package moe.seikimo.droplet.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.nbt.NbtMap;

@Getter
@RequiredArgsConstructor
public final class BlockState {
    private final int bedrockRuntimeId;
    private final NbtMap javaState, bedrockState;
    private final String javaIdentifier, bedrockIdentifier;
}
