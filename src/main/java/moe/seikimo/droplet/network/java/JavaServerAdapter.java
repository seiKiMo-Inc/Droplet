package moe.seikimo.droplet.network.java;

import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;

public final class JavaServerAdapter extends ServerAdapter {
    @Override
    public void sessionAdded(SessionAddedEvent event) {
        event.getSession().addListener(new JavaSessionAdapter());
    }
}
