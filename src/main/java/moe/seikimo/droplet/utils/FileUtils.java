package moe.seikimo.droplet.utils;

import io.netty.buffer.Unpooled;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;

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

    /**
     * Reads a packet from a file.
     *
     * @param packetId The ID of the packet.
     * @param file The file.
     * @return The packet.
     */
    static UnknownPacket readPacket(int packetId, File file) {
        try {
            return readPacket(packetId, new FileInputStream(file));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Reads a Bedrock packet from a stream.
     *
     * @param packetId The ID of the packet.
     * @param stream The stream.
     * @return The packet.
     */
    static UnknownPacket readPacket(int packetId, InputStream stream) {
        try {
            var bytes = stream.readAllBytes();
            var buffer = Unpooled.wrappedBuffer(bytes);

            var packet = new UnknownPacket();
            packet.setPacketId(packetId);
            packet.setPayload(buffer);
            return packet;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
