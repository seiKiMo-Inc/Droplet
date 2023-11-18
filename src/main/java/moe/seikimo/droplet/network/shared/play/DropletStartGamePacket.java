package moe.seikimo.droplet.network.shared.play;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerSpawnInfo;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.network.shared.BasePacket;
import moe.seikimo.droplet.utils.enums.Dimension;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

import java.util.List;
import java.util.UUID;

import static moe.seikimo.droplet.utils.ConversionUtils.convert;

/**
 * @see StartGamePacket For Bedrock clients.
 * @see <a href="https://github.com/Sandertv/gophertunnel/blob/master/minecraft/protocol/packet/start_game.go">Bedrock Documentation</a>
 * @see ClientboundLoginPacket For Java clients.
 * @see <a href="https://wiki.vg/Protocol#Login_Acknowledged">Java Documentation</a>
 */
@RequiredArgsConstructor
public final class DropletStartGamePacket extends BasePacket {
    private final int entityId; // Entity ID; Runtime Entity ID
    private final boolean isHardcore; // Is Hardcore; (no equivalent)
    private final GameMode gameMode; // Game Mode; Player Gamemode
    private final Dimension dimension; // Dimension Name; Dimension

    @Override
    public BedrockPacket toBedrock() {
        var server = Server.getInstance();
        var config = server.getConfig();

        var packet = new StartGamePacket();
        packet.setUniqueEntityId(this.entityId);
        packet.setRuntimeEntityId(this.entityId);
        packet.setPlayerGameType(convert(this.gameMode));
        packet.setPlayerPosition(Vector3f.ZERO);
        packet.setRotation(Vector2f.ZERO);
        packet.setSeed(0);
        packet.setSpawnBiomeType(SpawnBiomeType.DEFAULT);
        packet.setCustomBiomeName("plains");
        packet.setDimensionId(this.dimension.getId());
        packet.setGeneratorId(0);
        packet.setDefaultSpawn(Vector3i.ZERO);
        packet.setAchievementsDisabled(false);
        // setEditorWorldType
        packet.setCreatedInEditor(false);
        packet.setExportedFromEditor(false);
        packet.setDayCycleStopTime(0);
        packet.setEduEditionOffers(0);
        packet.setEduFeaturesEnabled(true);
        packet.setEducationProductionId("");
        packet.setRainLevel(0f);
        packet.setLightningLevel(0f);
        packet.setPlatformLockedContentConfirmed(false);
        packet.setMultiplayerGame(true);
        packet.setBroadcastingToLan(false);
        packet.setXblBroadcastMode(GamePublishSetting.FRIENDS_OF_FRIENDS);
        packet.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        packet.setCommandsEnabled(true);
        packet.setTexturePacksRequired(true);
        // setGamerules
        // setExperiments
        packet.setExperimentsPreviouslyToggled(true);
        packet.setBonusChestEnabled(false);
        packet.setStartingWithMap(false);
        packet.setDefaultPlayerPermission(PlayerPermission.CUSTOM);
        packet.setServerChunkTickRange(0);
        packet.setBehaviorPackLocked(false);
        packet.setResourcePackLocked(false);
        packet.setFromLockedWorldTemplate(false);
        packet.setUsingMsaGamertagsOnly(false);
        packet.setFromWorldTemplate(false);
        packet.setWorldTemplateOptionLocked(false);
        packet.setOnlySpawningV1Villagers(false);
        packet.setDisablingPersonas(false);
        packet.setDisablingCustomSkins(false);
        packet.setEmoteChatMuted(true);
        packet.setVanillaVersion("1.20.41");
        packet.setLimitedWorldWidth(0);
        packet.setLimitedWorldHeight(0);
        packet.setNetherType(true);
        packet.setEduSharedUriResource(EduSharedUriResource.EMPTY);
        packet.setForceExperimentalGameplay(OptionalBoolean.empty());
        packet.setLevelId("MA=="); // 0 in Base64
        packet.setLevelName(config.getString("server.name", "Droplet"));
        packet.setWorldTemplateId(UUID.randomUUID());
        packet.setTrial(false);
        packet.setAuthoritativeMovementMode(AuthoritativeMovementMode.CLIENT);
        packet.setCurrentTick(0L);
        packet.setEnchantmentSeed(0);
        // TODO: Send custom blocks to the client.
        packet.setBlockPalette(new NbtList<>(NbtType.COMPOUND, NbtMap.EMPTY));
        // TODO: Send vanilla items to the client.
        // TODO: Send custom items to the client.
        packet.setItemDefinitions(List.of());
        packet.setMultiplayerCorrelationId(UUID.randomUUID().toString());
        packet.setInventoriesServerAuthoritative(false);
        // setGameVersion
        // TODO: Send player entity properties.
        packet.setPlayerPropertyData(NbtMap.EMPTY);
        packet.setBlockRegistryChecksum(0);
        packet.setClientSideGenerationEnabled(false);
        packet.setWorldTemplateId(UUID.randomUUID());
        packet.setChatRestrictionLevel(ChatRestrictionLevel.NONE);
        packet.setDisablingPlayerInteractions(false);
        packet.setBlockNetworkIdsHashed(false);
        // setServerAuthoritativeSound

        return packet;
    }

    @Override
    public Packet toJava() {
        var server = Server.getInstance();
        var config = server.getConfig();

        return new ClientboundLoginPacket(
                this.entityId,
                this.isHardcore,
                new String[] {"minecraft:world"}, // World Names
                0, // Max Players (unused)
                config.getInt("world.max_render_distance", 32),
                config.getInt("world.max_simulation_distance", 32),
                config.getBoolean("player.show_debug_info", false),
                false, // Enable respawn screen
                true, // Limited crafting
                new PlayerSpawnInfo(
                        this.dimension.getName(),
                        "minecraft:world", // World name
                        0, // World seed
                        this.gameMode,
                        null, // Previous GameMode
                        false, // Debug world
                        true, // Flat world
                        null, // Last death position
                        0 // Portal cooldown
                )
        );
    }
}
