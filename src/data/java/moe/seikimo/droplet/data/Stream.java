package moe.seikimo.droplet.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public interface Stream {
    /**
     * Creates a new NBT input stream from the given file.
     *
     * @param file The file to read from.
     * @return A new NBT input stream.
     * @throws IOException If an I/O error occurs.
     */
    static NBTInputStream nbt(File file) throws IOException {
        return new NBTInputStream(new DataInputStream(
                        new GZIPInputStream(new FileInputStream(file))),
                true, true);
    }

    /**
     * Creates a new NBT output stream from the given file.
     *
     * @param file The file to write to.
     * @return A new NBT output stream.
     * @throws IOException If an I/O error occurs.
     */
    static NBTOutputStream oNbt(File file) throws IOException {
        return new NBTOutputStream(new DataOutputStream(
                new GZIPOutputStream(new FileOutputStream(file))));
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
