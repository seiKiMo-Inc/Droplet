package moe.seikimo.droplet.utils;

import java.io.InputStream;

public interface FileUtils {
    /**
     * Gets a resource from the classpath.
     *
     * @param path The path.
     * @return The resource.
     */
    static InputStream resource(String path) {
        return FileUtils.class.getClassLoader().getResourceAsStream(path);
    }
}
