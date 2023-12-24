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
}
