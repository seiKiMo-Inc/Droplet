package moe.seikimo.droplet.utils.constants;

import org.cloudburstmc.protocol.bedrock.data.Ability;

import java.util.EnumSet;
import java.util.Set;

public interface NetworkConstants {
    Set<String> BLACKLISTED_PACKETS = Set.of(
//            "LevelChunkPacket",
            "PlayerAuthInputPacket"
    );

    Set<String> UNSENDED_PACKETS = Set.of(
            "SetTimePacket",
            "InventoryContentPacket"
    );

    /**
     * These are all abilities presented to the client.
     * According to PMMP, they are *all* required.
     */
    Set<Ability> BASE_LAYER = EnumSet.of(
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
}
