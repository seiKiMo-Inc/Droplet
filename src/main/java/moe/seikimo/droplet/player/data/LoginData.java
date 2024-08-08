package moe.seikimo.droplet.player.data;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.seikimo.droplet.player.DropletSkin;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.utils.enums.Device;
import moe.seikimo.droplet.utils.enums.UIProfile;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationExpressionType;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Builder
public record LoginData(
        boolean xboxAuth,
        UUID uuid, String xuid,
        String displayName,
        DropletSkin skin,
        DeviceInfo deviceInfo,
        String language,
        String publicKey
) {
    @Getter
    private static class ExtraChainData {
        public String displayName, identity, XUID;
    }

    /**
     * This class is NOT skin data.
     * While it contains *some* skin data, it mostly comprises client data.
     * This is taken from 'KingRainbow44/Breaking-Bedrock'.
     */
    @Getter
    public static class SkinData {
        @SerializedName("AnimatedImageData")
        public List<SkinAnimation> animatedImageData = new ArrayList<>();

        @SerializedName("ArmSize")
        public ArmSizeType armSize = ArmSizeType.WIDE;

        @SerializedName("CapeData")
        public String capeData = "";

        @SerializedName("CapeId")
        public String capeId = "";

        @SerializedName("CapeImageHeight")
        public int capeImageHeight = 0;

        @SerializedName("CapeImageWidth")
        public int capeImageWidth = 0;

        @SerializedName("CapeOnClassicSkin")
        public boolean capeOnClassicSkin = false;

        @SerializedName("ClientRandomId")
        public long clientRandomId = new Random().nextLong();

        @SerializedName("CompatibleWithClientSideChunkGen")
        public boolean compatibleWithClientSideChunkGen = false;

        @SerializedName("CurrentInputMode")
        public int currentInputMode = 1;

        @SerializedName("DefaultInputMode")
        public int defaultInputMode = 1;

        @SerializedName("DeviceId")
        public String deviceId = UUID.randomUUID().toString();

        @SerializedName("DeviceModel")
        public String deviceModel = "Generic Android 10 Device";

        @SerializedName("DeviceOS")
        public int deviceOS = 1; // 7 = Windows 10, 1 = Android

        @SerializedName("GameVersion")
        public String gameVersion = null;

        @SerializedName("GuiScale")
        public int guiScale = 0;

        @SerializedName("IsEditorMode")
        public boolean isEditorMode = false;

        @SerializedName("LanguageCode")
        public String languageCode = "en_US";

        @SerializedName("OverrideSkin")
        public boolean overrideSkin = false;

        @SerializedName("PersonaPieces")
        public List<PersonaPiece> personaPieces = new ArrayList<>();

        @SerializedName("PersonaSkin")
        public boolean personaSkin = false;

        @SerializedName("PieceTintColors")
        public List<PersonaPieceTintColor> pieceTintColors = new ArrayList<>();

        @SerializedName("PlatformOfflineId")
        public String platformOfflineId = "";

        @SerializedName("PlatformOnlineId")
        public String platformOnlineId = "";

        @SerializedName("PlayFabId")
        public String playFabId = "";

        @SerializedName("PremiumSkin")
        public boolean premiumSkin = false;

        @SerializedName("SelfSignedId")
        public String selfSignedId = UUID.randomUUID().toString();

        @SerializedName("ServerAddress")
        public String serverAddress = "";

        @SerializedName("SkinAnimationData")
        public String skinAnimationData = "";

        @SerializedName("SkinColor")
        public String skinColor = "#0";

        @SerializedName("SkinData")
        public String skinData = null;

        @SerializedName("SkinGeometryData")
        public String skinGeometryData = null;

        @SerializedName("SkinGeometryDataEngineVersion")
        public String skinGeometryDataEngineVersion = "MC4wLjA=";

        @SerializedName("SkinId")
        public String skinId = STR."\{UUID.randomUUID()}.Custom";

        @SerializedName("SkinImageHeight")
        public int skinImageHeight = 64;

        @SerializedName("SkinImageWidth")
        public int skinImageWidth = 64;

        @SerializedName("SkinResourcePatch")
        public String skinResourcePatch = "ewogICAiZ2VvbWV0cnkiIDogewogICAgICAiZGVmYXVsdCIgOiAiZ2VvbWV0cnkuaHVtYW5vaWQuY3VzdG9tIgogICB9Cn0K";

        @SerializedName("ThirdPartyName")
        public String thirdPartyName = "";

        @SerializedName("ThirdPartyNameOnly")
        public boolean thirdPartyNameOnly = false;

        @SerializedName("TrustedSkin")
        public boolean trustedSkin = false;

        @SerializedName("UIProfile")
        public int uiProfile = 0;
    }

    public record SkinAnimation(@SerializedName("Frames") float frames,
                                @SerializedName("Image") String image,
                                @SerializedName("ImageHeight") int height,
                                @SerializedName("ImageWidth") int width,
                                @SerializedName("Type") AnimatedTextureType animatedTexture,
                                @SerializedName("AnimationExpression") AnimationExpressionType animationExpression) {
    }

    public record PersonaPiece(@SerializedName("IsDefault") boolean isDefault,
                               @SerializedName("PackId") String packId,
                               @SerializedName("PieceId") String pieceId,
                               @SerializedName("PieceType") String pieceType,
                               @SerializedName("ProductId") String productId) {
    }

    public record PersonaPieceTintColor(@SerializedName("PieceType") String pieceType,
                                        @SerializedName("Colors") List<String> colors) {
    }

    @Getter
    @RequiredArgsConstructor
    public enum ArmSizeType {
        @SerializedName("wide") WIDE("geometry.humanoid.custom", "default", "https://raw.githubusercontent.com/Flonja/TunnelMC/master/resources/steve.png"),
        @SerializedName("slim") SLIM("geometry.humanoid.customSlim", "slim", "https://raw.githubusercontent.com/Flonja/TunnelMC/master/resources/alex.png");

        private final String geometryName;
        private final String model;
        private final String defaultSkinUrl;

        /**
         * @return The encoded geometry data.
         */
        public String getEncodedGeometryData() {
            return EncodingUtils.base64Encode(STR."{\"geometry\":{\"default\":\"\{geometryName}" + "\"}}");
        }

        public static ArmSizeType fromUUID(UUID uuid) {
            return (uuid.hashCode() & 1) == 1 ? SLIM : WIDE;
        }
    }

    /**
     * Parses the individual parts of the login packet.
     *
     * @param packet The login packet.
     * @return The parsed login data.
     */
    public static LoginData from(LoginPacket packet) {
        var chainData = packet.getChain(); // Chain data contains all authentication-related data.
        var skinData = packet.getExtra(); // Skin is misleading; it also contains client data.
        var loginData = LoginData.builder();

        // Check if the chain is valid. (Xbox Live authentication)
        try {
            var chain = EncryptionUtils.validateChain(chainData);
            loginData.xboxAuth(chain.signed());
        } catch (Exception ignored) {
            loginData.xboxAuth(false);
        }

        {
            // Decode the other parts of the chain data.
            for (var chain : chainData) {
                var data = EncodingUtils.jwtDecode(chain);
                if (data == null) {
                    log.warn("Invalid JWT data for login packet.");
                    continue;
                }

                if (data.has("extraData")) {
                    var extraData = EncodingUtils.jsonDecode(
                            data.get("extraData"), ExtraChainData.class);
                    loginData
                            .displayName(extraData.getDisplayName())
                            .uuid(UUID.fromString(extraData.getIdentity()))
                            .xuid(extraData.getXUID());
                }

                loginData.publicKey(data.get("identityPublicKey").getAsString());
            }
        }

        {
            // Decode the skin data.
            var data = EncodingUtils.jwtDecode(skinData, SkinData.class);
            var deviceInfo = new DeviceInfo(
                    data.getDeviceModel(),
                    data.getDeviceId(),
                    data.getClientRandomId(),
                    Device.fromId(data.getDeviceOS()),
                    UIProfile.fromId(data.getUiProfile())
            );

            loginData
                    .deviceInfo(deviceInfo)
                    .language(data.getLanguageCode())
                    .skin(DropletSkin.from(data));
        }

        return loginData.build();
    }
}
