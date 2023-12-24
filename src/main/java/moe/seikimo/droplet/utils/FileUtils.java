package moe.seikimo.droplet.utils;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface FileUtils {
    /**
     * Gets a resource from the classpath.
     *
     * @param path The path.
     * @return The resource.
     */
    static InputStream resource(String path) {
        return FileUtils.class.getClassLoader()
                .getResourceAsStream(path);
    }

    /**
     * Reads an NBT file.
     *
     * @param file The file.
     * @return The NBT.
     */
    static NbtMap readNbt(File file) {
        try (var stream = NbtUtils.createGZIPReader(new FileInputStream(file))) {
            return (NbtMap) stream.readTag();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
