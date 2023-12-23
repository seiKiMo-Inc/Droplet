package moe.seikimo.droplet.world.io;

import moe.seikimo.droplet.world.World;

import java.io.File;

public interface WorldReader {
    /**
     * Reads a world file/directory.
     *
     * @param world The path to the file or directory.
     */
    World read(File world) throws Exception;
}
