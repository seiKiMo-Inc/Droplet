package moe.seikimo.droplet.utils;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import org.cloudburstmc.protocol.bedrock.data.GameType;

public interface ConversionUtils {
    /**
     * Converts a Java GameMode to a Bedrock GameType.
     *
     * @param gameMode The Java GameMode to convert.
     * @return The Bedrock GameType equivalent to the Java GameMode.
     */
    static GameType convert(GameMode gameMode) {
        return GameType.valueOf(gameMode.name());
    }
}
