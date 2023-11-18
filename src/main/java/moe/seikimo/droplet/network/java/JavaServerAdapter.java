package moe.seikimo.droplet.network.java;

import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class JavaServerAdapter extends ServerAdapter {
    private final JavaInterface netInterface;

    @Override
    public void sessionAdded(SessionAddedEvent event) {
        event.getSession().addListener(new JavaSessionAdapter(this.netInterface));
    }
}
