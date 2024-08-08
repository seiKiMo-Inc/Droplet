package moe.seikimo.droplet.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Device {
    UNKNOWN(-1),
    JAVA(0),
    ANDROID(1),
    IOS(2),
    OSX(3),
    AMAZON(4),
    GEAR_VR(5),
    HOLOLENS(6),
    WINDOWS(7),
    WINDOWS_32(8),
    DEDICATED(9),
    TVOS(10),
    PLAYSTATION(11),
    NINTENDO(12),
    XBOX(13),
    WINDOWS_PHONE(14);

    final int id;

    /**
     * @return True if the device is under a 'Bedrock' platform.
     */
    public boolean isBedrock() {
        return this != Device.JAVA;
    }

    /**
     * Converts the device ID to a device enum.
     *
     * @param id The device ID.
     * @return The device enum. (returns UNKNOWN if the ID is invalid)
     */
    public static Device fromId(int id) {
        return switch (id) {
            case 1 -> ANDROID;
            case 2 -> IOS;
            case 3 -> OSX;
            case 4 -> AMAZON;
            case 5 -> GEAR_VR;
            case 6 -> HOLOLENS;
            case 7 -> WINDOWS;
            case 8 -> WINDOWS_32;
            case 9 -> DEDICATED;
            case 10 -> TVOS;
            case 11 -> PLAYSTATION;
            case 12 -> NINTENDO;
            case 13 -> XBOX;
            case 14 -> WINDOWS_PHONE;
            default -> UNKNOWN;
        };
    }
}
