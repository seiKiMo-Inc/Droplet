package moe.seikimo.droplet.network.java;

import com.github.steveice10.packetlib.Session;
import lombok.RequiredArgsConstructor;
import moe.seikimo.droplet.network.NetworkSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class JavaNetworkSession extends NetworkSession {
    private static final Map<Session, JavaNetworkSession> sessions
            = new ConcurrentHashMap<>();

    /**
     * Get the {@link JavaNetworkSession} for the given {@link Session}.
     *
     * @param session The session.
     * @return The network session.
     */
    public static JavaNetworkSession from(Session session) {
        return sessions.computeIfAbsent(session, JavaNetworkSession::new);
    }

    private final Session handle;
}
