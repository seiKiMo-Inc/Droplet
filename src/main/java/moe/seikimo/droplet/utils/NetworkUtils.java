package moe.seikimo.droplet.utils;

/**
 * Code taken from <a href="https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/util/PluginMessageUtils.java">...</a>
 */
public interface NetworkUtils {
    /**
     * Creates a var-int byte array from the given value.
     *
     * @param value The value.
     * @return The byte array.
     */
    static byte[] getVarInt(int value) {
        var data = new byte[NetworkUtils.getVarIntLength(value)];

        var index = 0;
        while (value != 0) {
            var temp = (byte) (value & 0b01111111);

            value >>>= 7;
            if (value != 0) {
                temp |= (byte) 0b10000000;
            }

            data[index] = temp;
            index++;
        }

        return data;
    }

    /**
     * Returns the amount of bytes for a var-int of the given number.
     *
     * @param number The number.
     * @return The amount of bytes.
     */
    static int getVarIntLength(int number) {
        if ((number & 0xFFFFFF80) == 0) {
            return 1;
        } else if ((number & 0xFFFFC000) == 0) {
            return 2;
        } else if ((number & 0xFFE00000) == 0) {
            return 3;
        } else if ((number & 0xF0000000) == 0) {
            return 4;
        }
        return 5;
    }
}
