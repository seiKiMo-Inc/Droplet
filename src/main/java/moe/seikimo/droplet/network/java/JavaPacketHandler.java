package moe.seikimo.droplet.network.java;

import com.github.steveice10.packetlib.packet.Packet;
import moe.seikimo.droplet.network.NetworkSession;
import moe.seikimo.droplet.network.java.handlers.JavaPlayerActionHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class JavaPacketHandler<T extends Packet> {
    private static final Map<
            Class<? extends Packet>,
            JavaPacketHandler<? extends Packet>> packets
            = new HashMap<>();

    /**
     * Register all packet handlers.
     */
    public static void registerPackets() {
        JavaPacketHandler.registerHandler(JavaPlayerActionHandler.class);
    }

    /**
     * Registers a unique packet handler.
     *
     * @param handler The handler to register.
     */
    private static void registerHandler(Class<? extends JavaPacketHandler<?>> handler) {
        try {
            // Create an instance of the handler.
            var instance = handler.getConstructor().newInstance();
            // Get the class of the handler's packet.
            var packetClass = instance.getPacketClass();

            // Register the handler.
            packets.put(packetClass, instance);
        } catch (Exception ignored) { }
    }

    /**
     * Processes a packet.
     *
     * @param networkSession The session the packet was received from.
     * @param packet The packet to process.
     */
    public static void processPacket(NetworkSession networkSession, Packet packet) {
        // Get the handler.
        var handler = packets.get(packet.getClass());
        // If the handler exists, handle the packet.
        if (handler != null) {
            handler.handle(networkSession, packet);
        }
    }

    /**
     * @return The class of the packet this handler handles.
     */
    protected abstract Class<? extends Packet> getPacketClass();

    /**
     * Handles a packet.
     * Uses the class's type parameter to cast the packet.
     *
     * @param packet The instance of the packet to handle.
     */
    @SuppressWarnings("unchecked")
    public void handle(NetworkSession networkSession, Object packet) {
        this.handle(networkSession, (T) packet);
    }

    /**
     * Handles a packet.
     * This method is called by {@link #handle(NetworkSession, Object)}.
     *
     * @param session The session the packet was received from.
     * @param packet The instance of the packet to handle.
     */
    protected abstract void handle(NetworkSession session, T packet);
}
