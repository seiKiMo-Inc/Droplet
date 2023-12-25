package moe.seikimo.droplet.utils;

import java.io.File;

public interface Preconditions {
    /**
     * Checks if the given file exists.
     *
     * @param file The file to check.
     */
    static void fileExists(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " does not exist.");
        }
    }

    /**
     * Checks if the given file is a directory.
     *
     * @param file The file to check.
     */
    static void canReadFile(File file) {
        if (!file.canRead()) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " cannot be read.");
        }
    }

    /**
     * Checks if the given file is a directory.
     *
     * @param file The file to check.
     */
    static void notDirectory(File file) {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " is a directory.");
        }
    }

    /**
     * Checks if the given file is a directory.
     *
     * @param file The file to check.
     */
    static void isDirectory(File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " is not a directory.");
        }
    }

    /**
     * Checks if the given number is within the bounds.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @param value The value to check.
     */
    static void inclusive(long min, long max, long value) {
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value " + value + " is not between " + min + " and " + max + ".");
        }
    }
}
