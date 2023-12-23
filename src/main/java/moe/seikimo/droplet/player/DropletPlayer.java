package moe.seikimo.droplet.player;

import lombok.Getter;
import lombok.NonNull;
import moe.seikimo.droplet.entity.DropletLivingEntity;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.utils.enums.Platform;
import moe.seikimo.droplet.world.World;

@Getter
public class DropletPlayer extends DropletLivingEntity implements Player {
    private final NetworkSession networkSession;
    private final Platform platform;

    @NonNull private World world;

    public DropletPlayer(long entityId, World world, NetworkSession networkSession, Platform platform) {
        super(entityId);

        this.networkSession = networkSession;
        this.platform = platform;
    }
}
