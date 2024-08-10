package moe.seikimo.droplet.utils.constants;

import moe.seikimo.droplet.utils.NetworkUtils;
import org.cloudburstmc.protocol.bedrock.data.Ability;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Set;

public final class NetworkConstants {
    public static final byte[] BRANDING;

    public static final Set<String> BLACKLISTED_PACKETS = Set.of(
            "PlayerAuthInputPacket"
    );

    public static final Set<String> UNSENDED_PACKETS = Set.of(
            "SetTimePacket",
            "InventoryContentPacket"
    );

    /**
     * These are all abilities presented to the client.
     * According to PMMP, they are *all* required.
     */
    public static final Set<Ability> BASE_LAYER = EnumSet.of(
            Ability.MAY_FLY,
            Ability.FLYING,
            Ability.NO_CLIP,
            Ability.OPERATOR_COMMANDS,
            Ability.TELEPORT,
            Ability.INVULNERABLE,
            Ability.MUTED,
            Ability.WORLD_BUILDER,
            Ability.INSTABUILD,
            Ability.LIGHTNING,
            Ability.BUILD,
            Ability.MINE,
            Ability.DOORS_AND_SWITCHES,
            Ability.OPEN_CONTAINERS,
            Ability.ATTACK_PLAYERS,
            Ability.ATTACK_MOBS,
            Ability.PRIVILEGED_BUILDER
    );

    static {
        /// <editor-fold desc="Branding">
        var name = "Droplet".getBytes(StandardCharsets.UTF_8);
        BRANDING = ByteBuffer
                .allocate(name.length + NetworkUtils.getVarIntLength(name.length))
                .put(NetworkUtils.getVarInt(name.length))
                .put(name)
                .array();
        /// </editor-fold>
    }
}
