package moe.seikimo.droplet.network.java;

import lombok.RequiredArgsConstructor;
import org.geysermc.mcprotocollib.network.event.server.ServerAdapter;
import org.geysermc.mcprotocollib.network.event.server.SessionAddedEvent;

@RequiredArgsConstructor
public final class JavaServerAdapter extends ServerAdapter {
    private final JavaInterface netInterface;

    @Override
    public void sessionAdded(SessionAddedEvent event) {
        event.getSession().addListener(new JavaSessionAdapter(this.netInterface));
    }
}
