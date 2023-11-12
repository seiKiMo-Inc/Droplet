package moe.seikimo.droplet.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.utils.enums.Platform;
import moe.seikimo.droplet.world.World;

@Getter
@RequiredArgsConstructor
public class DropletPlayer implements Player {
    private final NetworkSession networkSession;
    private final Platform platform;

    @NonNull private World world;
}
