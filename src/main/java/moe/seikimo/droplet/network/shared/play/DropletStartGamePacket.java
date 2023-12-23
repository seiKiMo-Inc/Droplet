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

import java.util.Collection;
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
    public Collection<BedrockPacket> toBedrock() {
        var server = Server.getInstance();
        var config = server.getConfig();

        var gamePacket = new StartGamePacket();
        gamePacket.setUniqueEntityId(this.entityId);
        gamePacket.setRuntimeEntityId(this.entityId);
        gamePacket.setPlayerGameType(convert(this.gameMode));
        gamePacket.setPlayerPosition(Vector3f.ZERO);
        gamePacket.setRotation(Vector2f.ZERO);
        gamePacket.setSeed(0);
        gamePacket.setSpawnBiomeType(SpawnBiomeType.DEFAULT);
        gamePacket.setCustomBiomeName("plains");
        gamePacket.setDimensionId(this.dimension.getId());
        gamePacket.setGeneratorId(0);
        gamePacket.setLevelGameType(GameType.SURVIVAL);
        gamePacket.setDifficulty(1);
        gamePacket.setDefaultSpawn(Vector3i.ZERO);
        gamePacket.setAchievementsDisabled(false);
        // setEditorWorldType
        gamePacket.setCreatedInEditor(false);
        gamePacket.setExportedFromEditor(false);
        gamePacket.setDayCycleStopTime(0);
        gamePacket.setEduEditionOffers(0);
        gamePacket.setEduFeaturesEnabled(true);
        gamePacket.setEducationProductionId("");
        gamePacket.setRainLevel(0f);
        gamePacket.setLightningLevel(0f);
        gamePacket.setPlatformLockedContentConfirmed(false);
        gamePacket.setMultiplayerGame(true);
        gamePacket.setBroadcastingToLan(false);
        gamePacket.setXblBroadcastMode(GamePublishSetting.FRIENDS_OF_FRIENDS);
        gamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        gamePacket.setCommandsEnabled(true);
        gamePacket.setTexturePacksRequired(true);
        // setGamerules
        // setExperiments
        gamePacket.setExperimentsPreviouslyToggled(true);
        gamePacket.setBonusChestEnabled(false);
        gamePacket.setStartingWithMap(false);
        gamePacket.setDefaultPlayerPermission(PlayerPermission.CUSTOM);
        gamePacket.setServerChunkTickRange(0);
        gamePacket.setBehaviorPackLocked(false);
        gamePacket.setResourcePackLocked(false);
        gamePacket.setFromLockedWorldTemplate(false);
        gamePacket.setUsingMsaGamertagsOnly(false);
        gamePacket.setFromWorldTemplate(false);
        gamePacket.setWorldTemplateOptionLocked(false);
        gamePacket.setOnlySpawningV1Villagers(false);
        gamePacket.setDisablingPersonas(false);
        gamePacket.setDisablingCustomSkins(false);
        gamePacket.setEmoteChatMuted(true);
        gamePacket.setVanillaVersion("1.20.41");
        gamePacket.setLimitedWorldWidth(0);
        gamePacket.setLimitedWorldHeight(0);
        gamePacket.setNetherType(true);
        gamePacket.setEduSharedUriResource(EduSharedUriResource.EMPTY);
        gamePacket.setForceExperimentalGameplay(OptionalBoolean.empty());
        gamePacket.setLevelId("MA=="); // 0 in Base64
        gamePacket.setLevelName(config.getString("server.name", "Droplet"));
        gamePacket.setPremiumWorldTemplateId(UUID.randomUUID().toString());
        gamePacket.setWorldTemplateId(UUID.randomUUID());
        gamePacket.setTrial(false);
        gamePacket.setAuthoritativeMovementMode(AuthoritativeMovementMode.CLIENT);
        gamePacket.setCurrentTick(0L);
        gamePacket.setEnchantmentSeed(0);
        // TODO: Send custom blocks to the client.
        gamePacket.setBlockPalette(new NbtList<>(NbtType.COMPOUND, NbtMap.EMPTY));
        // TODO: Send custom items to the client.
        gamePacket.setItemDefinitions(server.getItemManager().getDefinitionsList());
        gamePacket.setMultiplayerCorrelationId(UUID.randomUUID().toString());
        gamePacket.setInventoriesServerAuthoritative(false);
        // setGameVersion
        // TODO: Send player entity properties.
        gamePacket.setPlayerPropertyData(NbtMap.EMPTY);
        gamePacket.setBlockRegistryChecksum(0);
        gamePacket.setClientSideGenerationEnabled(false);
        gamePacket.setWorldTemplateId(UUID.randomUUID());
        gamePacket.setChatRestrictionLevel(ChatRestrictionLevel.NONE);
        gamePacket.setDisablingPlayerInteractions(false);
        gamePacket.setBlockNetworkIdsHashed(false);
        // setServerAuthoritativeSound
        gamePacket.setServerEngine("Droplet");

        return List.of(gamePacket);
    }

    @Override
    public Collection<Packet> toJava() {
        var server = Server.getInstance();
        var config = server.getConfig();

        return List.of(new ClientboundLoginPacket(
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
        ));
    }
}
