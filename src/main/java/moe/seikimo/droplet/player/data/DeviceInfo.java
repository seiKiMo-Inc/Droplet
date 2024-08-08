package moe.seikimo.droplet.player.data;

import moe.seikimo.droplet.utils.enums.Device;
import moe.seikimo.droplet.utils.enums.UIProfile;
import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;

public record DeviceInfo(
        String deviceName,
        String deviceId,
        long clientId,
        Device device,
        UIProfile uiProfile
) {
    /**
     * Adds the device info to the packet entry.
     *
     * @param entry The packet entry.
     */
    public void addToEntry(PlayerListPacket.Entry entry) {
        entry.setPlatformChatId(this.deviceName());
        entry.setBuildPlatform(this.device().getId());
    }
}
