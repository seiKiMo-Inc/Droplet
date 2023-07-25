package moe.seikimo.droplet.network;

import io.netty.buffer.ByteBuf;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public interface NetworkInterface {
    /**
     * Blocks the given address from connecting to the server.
     * This is indefinite and will not be removed until the server is restarted.
     *
     * @param address The address to block.
     */
    void blockAddress(InetAddress address);

    /**
     * Blocks the given address from connecting to the server.
     * This will be removed after the given timeout.
     *
     * @param address The address to block.
     * @param timeout The timeout.
     * @param unit The unit of the timeout.
     */
    void blockAddress(InetAddress address, long timeout, TimeUnit unit);

    /**
     * Removes a block on the given address.
     *
     * @param address The address to unblock.
     */
    void unblockAddress(InetAddress address);

    /**
     * Sends a packet to the given address.
     *
     * @param address The address to send the packet to.
     * @param buffer The packet to send.
     */
    void sendPacket(InetSocketAddress address, ByteBuf buffer);

    /**
     * Sets the advertised name of the interface.
     * This is used for the server MOTD.
     *
     * @param name The name.
     * @param subName The sub-name.
     */
    void setName(String name, String subName);

    /**
     * Stops the interface.
     */
    void shutdown();

    /**
     * Immediately stops the interface.
     */
    void emergencyShutdown();
}
