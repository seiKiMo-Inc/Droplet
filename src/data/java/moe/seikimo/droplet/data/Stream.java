package moe.seikimo.droplet.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.cloudburstmc.nbt.NBTInputStream;

import java.io.*;

public interface Stream {
    /**
     * Creates a new NBT input stream from the given file.
     *
     * @param file The file to read from.
     * @return A new NBT input stream.
     * @throws IOException If an I/O error occurs.
     */
    static NBTInputStream nbt(File file) throws IOException {
        return new NBTInputStream(
                new DataInputStream(
                        new FileInputStream(file)));
    }

    /**
     * Creates a new JSON input stream from the given file.
     *
     * @param file The file to read from.
     * @return A new JSON input stream.
     * @throws IOException If an I/O error occurs.
     */
    static JsonObject json(File file) throws IOException {
        return new Gson().fromJson(new FileReader(file), JsonObject.class);
    }
}
