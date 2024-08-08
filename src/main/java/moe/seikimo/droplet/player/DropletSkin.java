package moe.seikimo.droplet.player;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import moe.seikimo.droplet.player.data.LoginData;
import moe.seikimo.droplet.utils.SkinUtils;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationData;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;

/**
 * Cross-platform player skin implementation.
 * A wrapper for {@link SerializedSkin}.
 */
@Data
@Slf4j
public final class DropletSkin {
    private final SerializedSkin handle;
    private final boolean trusted;

    /**
     * Creates a new skin.
     *
     * @param bedrock The Bedrock network-serialized skin.
     * @return The skin.
     */
    public static DropletSkin from(SerializedSkin bedrock) {
        return new DropletSkin(bedrock, false);
    }

    /**
     * Parses a skin from an input stream.
     *
     * @param stream The input stream.
     * @return The skin.
     */
    public static DropletSkin from(InputStream stream) {
        var skin = SerializedSkin.builder();
        try {
            var image = ImageIO.read(stream);
            skin.skinData(ImageData.from(image));
        } catch (IOException exception) {
            log.error("Failed to read skin image.", exception);
            return null;
        }

        return new DropletSkin(skin.build(), true);
    }

    /**
     * Parses a skin from a file.
     *
     * @param file The file.
     * @return The skin.
     */
    public static DropletSkin from(File file) {
        try {
            return DropletSkin.from(new FileInputStream(file));
        } catch (FileNotFoundException exception) {
            log.error("Failed to read skin file.", exception);
            return null;
        }
    }

    /**
     * Creates a Droplet skin from network login data.
     *
     * @param data The login data.
     * @return The Droplet skin.
     */
    public static DropletSkin from(LoginData.SkinData data) {
        var skin = SerializedSkin.builder()
                .skinId(data.getSkinId())
                .skinResourcePatch(data.getSkinResourcePatch())
                .geometryData(data.getSkinGeometryData())
                .animationData(data.getSkinAnimationData())
                .capeId(data.getCapeId())
                .skinColor(data.getSkinColor())
                .armSize(data.getArmSize().name())
                .playFabId(data.getPlayFabId())
                .skinData(SkinUtils.toImage(
                        data.getSkinData(),
                        data.getSkinImageWidth(),
                        data.getSkinImageHeight()
                ))
                .capeData(SkinUtils.toImage(
                        data.getCapeData(),
                        data.getCapeImageWidth(),
                        data.getCapeImageHeight()
                ))
                .premium(data.isPremiumSkin())
                .persona(data.isPersonaSkin())
                .capeOnClassic(data.isCapeOnClassicSkin())
                .animations(data.getAnimatedImageData().stream()
                        .map(SkinUtils::convert)
                        .toList())
                .personaPieces(data.getPersonaPieces().stream()
                        .map(SkinUtils::convert)
                        .toList())
                .tintColors(data.getPieceTintColors().stream()
                        .map(SkinUtils::convert)
                        .toList())
                .build();
        return DropletSkin.from(skin);
    }

    /**
     * Adds the device info to the packet entry.
     *
     * @param entry The packet entry.
     */
    public void addToEntry(PlayerListPacket.Entry entry) {
        entry.setSkin(this.getHandle());
        entry.setTrustedSkin(this.isTrusted());
    }
}
