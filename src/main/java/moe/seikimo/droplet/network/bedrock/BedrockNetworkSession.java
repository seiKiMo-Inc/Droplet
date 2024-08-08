package moe.seikimo.droplet.network.bedrock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.player.Player;
import moe.seikimo.droplet.utils.constants.NetworkConstants;
import moe.seikimo.droplet.utils.enums.Device;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.Ability;
import org.cloudburstmc.protocol.bedrock.data.AbilityLayer;
import org.cloudburstmc.protocol.bedrock.data.PlayerPermission;
import org.cloudburstmc.protocol.bedrock.data.command.CommandPermission;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAbilitiesPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAdventureSettingsPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static moe.seikimo.droplet.utils.constants.NetworkConstants.BLACKLISTED_PACKETS;
import static moe.seikimo.droplet.utils.constants.NetworkConstants.UNSENDED_PACKETS;

@Getter
@RequiredArgsConstructor
public final class BedrockNetworkSession extends NetworkSession implements BedrockPacketHandler {
    private static final Map<BedrockServerSession, BedrockNetworkSession> sessions
            = new ConcurrentHashMap<>();

    /**
     * Get the {@link BedrockNetworkSession} for the given {@link BedrockServerSession}.
     *
     * @param session The session.
     * @return The network session.
     */
    public static BedrockNetworkSession from(BedrockServerSession session) {
        return sessions.computeIfAbsent(session, k -> {
            var networkSession = new BedrockNetworkSession(k);
            networkSession.setDevice(Device.UNKNOWN);
            return networkSession;
        });
    }

    private final BedrockServerSession handle;

    @Setter private BedrockPacketHandler packetHandler;
    @Setter private Player player;

    /**
     * Sends the player's abilities to the client.
     * @see UpdateAbilitiesPacket
     */
    public void syncAbilities() {
        var player = this.getPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is not set.");
        }

        // Parse player ability layers.
        var abilities = new ArrayList<AbilityLayer>();

        /// <editor-fold desc="Base layer">

        var baseLayer = new AbilityLayer();
        baseLayer.setLayerType(AbilityLayer.Type.BASE);
        baseLayer.setFlySpeed(0.05f);
        baseLayer.setWalkSpeed(0.1f);
        baseLayer.getAbilitiesSet().addAll(NetworkConstants.BASE_LAYER);

        // TODO: Add abilities accordingly.
        var values = baseLayer.getAbilityValues();
        values.add(Ability.OPERATOR_COMMANDS);
        values.add(Ability.FLYING);
        values.add(Ability.MAY_FLY);
        values.add(Ability.BUILD);
        values.add(Ability.MINE);
        values.add(Ability.DOORS_AND_SWITCHES);
        values.add(Ability.OPEN_CONTAINERS);
        values.add(Ability.ATTACK_PLAYERS);
        values.add(Ability.ATTACK_MOBS);

        abilities.add(baseLayer);

        /// </editor-fold>

        // Prepare the packet.
        var packet = new UpdateAbilitiesPacket();
        packet.setCommandPermission(CommandPermission.GAME_DIRECTORS);
        packet.setPlayerPermission(PlayerPermission.OPERATOR);
        packet.setUniqueEntityId(player.getEntityId());
        packet.getAbilityLayers().addAll(abilities);

        this.sendPacket(packet);
    }

    /**
     * Sends the player's adventure settings to the client.
     * @see UpdateAdventureSettingsPacket
     */
    public void syncAdventureSettings() {
        // Prepare the packet.
        var adventureSettingsPacket = new UpdateAdventureSettingsPacket();
        adventureSettingsPacket.setShowNameTags(true);
        adventureSettingsPacket.setImmutableWorld(false);
        adventureSettingsPacket.setNoMvP(false);
        adventureSettingsPacket.setNoPvM(false);

        // TODO: Dynamically set the player's auto jump setting.
        adventureSettingsPacket.setAutoJump(true);

        this.sendPacket(adventureSettingsPacket);
    }

    @Override
    public PacketSignal handlePacket(BedrockPacket packet) {
        var packetName = packet.getClass().getSimpleName();
        if (!BLACKLISTED_PACKETS.contains(packetName)) {
            this.getLogger().debug("CLI -> SRV: {}", packetName);
        }

        return this.packetHandler.handlePacket(packet);
    }

    @Override
    public void sendPacket(BasePacket packet) {
        packet.toBedrock().forEach(this::sendPacket);
    }

    @Override
    public void sendPacket(BedrockPacket... packets) {
        for (var packet : packets) {
            var packetName = packet.getClass().getSimpleName();
            if (UNSENDED_PACKETS.contains(packetName)) continue;

            this.getHandle().sendPacket(packet);

            if (Server.getInstance().isLogPackets()) {
                if (BLACKLISTED_PACKETS.contains(packetName)) continue;
                this.getLogger().debug("SRV -> CLI: {}", packetName);
            }
        }
    }

    @Override
    public void sendPacketImmediately(BedrockPacket... packets) {
        for (var packet : packets) {
            var packetName = packet.getClass().getSimpleName();
            if (UNSENDED_PACKETS.contains(packetName)) continue;

            this.getHandle().sendPacketImmediately(packet);

            if (Server.getInstance().isLogPackets()) {
                if (BLACKLISTED_PACKETS.contains(packetName)) continue;
                this.getLogger().debug("SRV -> CLI: {}", packetName);
            }
        }
    }
}
