package moe.seikimo.droplet.utils.enums;

public enum UIProfile {
    CLASSIC,
    POCKET;

    /**
     * Converts the UI profile ID to a UI profile enum.
     *
     * @param id The UI profile ID.
     * @return The UI profile enum. (returns null if the ID is invalid)
     */
    public static UIProfile fromId(int id) {
        return switch (id) {
            case 0 -> CLASSIC;
            case 1 -> POCKET;
            default -> null;
        };
    }
}
