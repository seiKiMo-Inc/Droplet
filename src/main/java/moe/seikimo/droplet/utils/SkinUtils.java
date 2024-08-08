package moe.seikimo.droplet.utils;

import moe.seikimo.droplet.player.data.LoginData;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationData;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceTintData;

import javax.imageio.ImageIO;

public interface SkinUtils {
    /**
     * Parses an image from its Base64 and dimensions.
     *
     * @param data The Base64 data.
     * @param width The width of the image.
     * @param height The height of the image.
     * @return The parsed image.
     */
    static ImageData toImage(String data, int width, int height) {
        var bytes = EncodingUtils.base64Decode(data);
        return ImageData.of(width, height, bytes);
    }

    /**
     * Converts a {@link LoginData.SkinAnimation} to an {@link AnimationData}.
     *
     * @param animation The skin animation.
     * @return The animation data.
     */
    static AnimationData convert(LoginData.SkinAnimation animation) {
        var image = SkinUtils.toImage(
                animation.image(), animation.width(), animation.height());
        return new AnimationData(
                image,
                animation.animatedTexture(),
                animation.frames(),
                animation.animationExpression()
        );
    }

    /**
     * Converts a {@link LoginData.PersonaPiece} to a {@link PersonaPieceData}.
     *
     * @param piece The persona piece.
     * @return The persona piece data.
     */
    static PersonaPieceData convert(LoginData.PersonaPiece piece) {
        return new PersonaPieceData(
                piece.pieceId(),
                piece.pieceType(),
                piece.packId(),
                piece.isDefault(),
                piece.productId()
        );
    }

    /**
     * Converts a {@link LoginData.PersonaPieceTintColor} to a {@link PersonaPieceTintData}.
     *
     * @param tint The persona piece tint.
     * @return The persona piece tint data.
     */
    static PersonaPieceTintData convert(LoginData.PersonaPieceTintColor tint) {
        return new PersonaPieceTintData(tint.pieceType(), tint.colors());
    }
}
