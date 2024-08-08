package moe.seikimo.droplet.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import moe.seikimo.droplet.entity.DropletLivingEntity;
import moe.seikimo.droplet.inventory.DropletInventory;
import moe.seikimo.droplet.inventory.Inventory;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.player.data.DeviceInfo;
import moe.seikimo.droplet.player.impl.PlayerInventoryViewer;
import moe.seikimo.droplet.utils.enums.Device;
import moe.seikimo.droplet.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class DropletPlayer extends DropletLivingEntity
        implements Player, PlayerInventoryViewer {
    private final NetworkSession networkSession;
    private final Device platform;
    private final UUID uuid;

    @Setter private String displayName;
    @Setter private DeviceInfo deviceInfo;
    @Setter private String xuid;
    @Setter private DropletSkin skin;

    @NonNull @Setter
    private World world;

    @Setter private boolean spawned = false;
    @Setter private int renderDistance = 16;

    @Setter private Inventory inventory = new DropletInventory();

    public DropletPlayer(
            UUID uuid,
            String displayName,
            long entityId,
            @NotNull World world,
            NetworkSession networkSession,
            Device platform
    ) {
        super(entityId);

        this.networkSession = networkSession;
        this.platform = platform;
        this.uuid = uuid;

        this.world = world;
        this.displayName = displayName;
    }
}
