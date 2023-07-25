package moe.seikimo.droplet.network;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moe.seikimo.droplet.Server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class NetworkManager {
    private final Server server;

    private final List<NetworkInterface> interfaces
            = new ArrayList<>();

    @Setter private String name = "Droplet", subName = "Droplet Server";

    /**
     * Register a network interface.
     *
     * @param netInterface The network interface to register.
     */
    public void registerInterface(NetworkInterface netInterface) {
        this.interfaces.add(netInterface);
        netInterface.setName(this.name, this.subName);
    }

    /**
     * Sends a packet through all registered interfaces.
     *
     * @param address The address to send the packet to.
     * @param buffer The packet to send.
     */
    public void sendPacket(InetSocketAddress address, ByteBuf buffer) {
        this.interfaces.forEach(netInterface ->
                netInterface.sendPacket(address, buffer));
    }
}
