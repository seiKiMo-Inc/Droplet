package moe.seikimo.droplet.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import moe.seikimo.droplet.entity.DropletLivingEntity;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.utils.enums.Platform;
import moe.seikimo.droplet.world.World;
import org.jetbrains.annotations.NotNull;

@Getter
public class DropletPlayer extends DropletLivingEntity implements Player {
    private final NetworkSession networkSession;
    private final Platform platform;

    @NonNull @Setter
    private World world;

    @Setter private boolean spawned = false;
    @Setter private int renderDistance = 16;

    public DropletPlayer(
            long entityId,
            @NotNull World world,
            NetworkSession networkSession,
            Platform platform
    ) {
        super(entityId);

        this.networkSession = networkSession;
        this.platform = platform;
        this.world = world;
    }
}
