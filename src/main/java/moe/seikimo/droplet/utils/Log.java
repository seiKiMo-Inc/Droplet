package moe.seikimo.droplet.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import moe.seikimo.droplet.Server;
import org.slf4j.LoggerFactory;

public interface Log {
    /**
     * Enables debug logging for the given logger.
     *
     * @param logger The logger to enable debug logging for.
     */
    static void setDebug(Object logger) {
        if (!Server.isDebug()) return;

        if (logger instanceof Logger logback) {
            logback.setLevel(Level.DEBUG);
        }
    }

    /**
     * Creates a new logger with the given name.
     *
     * @param name The name of the logger.
     * @return The logger.
     */
    static org.slf4j.Logger newLogger(String name) {
        var logger = LoggerFactory.getLogger(name);
        Log.setDebug(logger);

        return logger;
    }
}
