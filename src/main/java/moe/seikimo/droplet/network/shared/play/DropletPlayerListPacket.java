package moe.seikimo.droplet.network.shared.play;

import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntryAction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoRemovePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoUpdatePacket;
import org.geysermc.mcprotocollib.network.packet.Packet;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.player.DropletSkin;
import moe.seikimo.droplet.player.data.DeviceInfo;
import net.kyori.adventure.text.Component;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.*;

/**
 * @see PlayerListPacket For Bedrock clients.
 * @see <a href="https://github.com/Sandertv/gophertunnel/blob/master/minecraft/protocol/packet/player_list.go">...</a>
 * @see ClientboundPlayerInfoUpdatePacket For Java clients.
 * @see <a href="https://wiki.vg/Protocol#Player_Info_Update">...</a>
 * @see ClientboundPlayerInfoRemovePacket For Java clients.
 * @see <a href="https://wiki.vg/Protocol#Player_Info_Remove">...</a>
 */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public final class DropletPlayerListPacket extends BasePacket {
    public final Action action; // action; (no equivalent)
    public final Collection<Entry> entry; // entries; entries

    /**
     * This should not be null if {@link DropletPlayerListPacket#action} is {@link Action#UPDATE}.
     */
    public EnumSet<UpdateAction> actions = null; // (no equivalent); actions

    @Override
    public Collection<BedrockPacket> toBedrock() {
        if (this.action == Action.UPDATE) {
            return Collections.emptyList();
        }

        var packet = new PlayerListPacket();
        packet.setAction(switch (this.action) {
            case ADD -> PlayerListPacket.Action.ADD;
            case REMOVE -> PlayerListPacket.Action.REMOVE;
            case UPDATE -> throw new UnsupportedOperationException("Cannot convert UPDATE action to Bedrock.");
        });

        for (var entry : this.entry) {
            packet.getEntries().add(entry.toBedrock());
        }
        return List.of(packet);
    }

    @Override
    public Collection<Packet> toJava() {
        // Handle a special case for the REMOVE action.
        if (this.action == Action.REMOVE) {
            return List.of(new ClientboundPlayerInfoRemovePacket(
                    this.entry.stream().map(Entry::getUuid).toList()
            ));
        }

        return List.of(new ClientboundPlayerInfoUpdatePacket(
                this.action.toJava(this.actions),
                this.entry.stream().map(Entry::toJava).toArray(PlayerListEntry[]::new)
        ));
    }

    @Slf4j
    @Builder
    public static final class Entry {
        @Getter @NotNull
        private final UUID uuid;

        private final String name;
        private final DropletSkin skin;

        /// <editor-fold desc="Java Entry">

        private boolean listed;
        private int latency;
        private GameMode gameMode;
        private UUID sessionId;
        private long expiresAt;
        private PublicKey publicKey;
        private byte[] keySignature;
        /**
         * This is an override handle for {@link Entry#skin}.
         */
        private GameProfile profile;

        /// </editor-fold>

        /// <editor-fold desc="Bedrock Entry">

        private long entityId;
        private String xuid;
        private DeviceInfo deviceInfo;

        /// </editor-fold>

        /**
         * @return A Java player list entry.
         */
        public PlayerListEntry toJava() {
            if (this.name == null) {
                return new PlayerListEntry(this.uuid);
            }

            var profile = this.profile != null ? this.profile :
                    new GameProfile(this.uuid, this.name);

            return new PlayerListEntry(
                    this.uuid, profile,
                    this.listed, this.latency,
                    this.gameMode, Component.text(this.name),
                    this.sessionId, this.expiresAt,
                    this.publicKey, this.keySignature
            );
        }

        /**
         * @return A Bedrock player list entry.
         */
        public PlayerListPacket.Entry toBedrock() {
            var entry = new PlayerListPacket.Entry(this.uuid);

            // This should only be the case if the entry is being removed.
            if (this.name == null) {
                return entry;
            }

            entry.setEntityId(this.entityId);
            entry.setName(this.name);
            entry.setXuid(this.xuid);
            this.deviceInfo.addToEntry(entry);
            this.skin.addToEntry(entry);

            return entry;
        }
    }

    public enum Action {
        ADD,
        REMOVE,
        UPDATE;

        /**
         * Serializes this action to a Java player list entry action.
         *
         * @return The Java player list entry action.
         */
        public EnumSet<PlayerListEntryAction> toJava(EnumSet<UpdateAction> actions) {
            return switch (this) {
                case ADD -> EnumSet.of(PlayerListEntryAction.ADD_PLAYER);
                case REMOVE -> throw new UnsupportedOperationException("Cannot convert UPDATE action to Java.");
                case UPDATE -> {
                    if (actions == null) {
                        throw new IllegalArgumentException("Actions must be provided for UPDATE action.");
                    }

                    var set = EnumSet.noneOf(PlayerListEntryAction.class);
                    for (var action : actions) {
                        switch (action) {
                            case GAME_MODE -> set.add(PlayerListEntryAction.UPDATE_GAME_MODE);
                            case LISTED -> set.add(PlayerListEntryAction.UPDATE_LISTED);
                            case LATENCY -> set.add(PlayerListEntryAction.UPDATE_LATENCY);
                            case DISPLAY_NAME -> set.add(PlayerListEntryAction.UPDATE_DISPLAY_NAME);
                        }
                    }
                    yield set;
                }
            };
        }
    }

    public enum UpdateAction {
        GAME_MODE,
        LISTED,
        LATENCY,
        DISPLAY_NAME
    }
}
