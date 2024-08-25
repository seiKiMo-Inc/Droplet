package moe.seikimo.droplet.commands;

import moe.seikimo.droplet.Server;
import moe.seikimo.droplet.player.Player;

/**
 * A source which could represent a player, a server console, or other kinds of command executors.
 */
public abstract class CommandSource {
    /**
     * @return The source as a server, if it's the server.
     */
    public Server asServer() {
        throw new IllegalArgumentException("This source is not a server.");
    }

    /**
     * @return The source as a player, if it's a player.
     */
    public Player asPlayer() {
        throw new IllegalArgumentException("This source is not a player.");
    }

    /**
     * Sends a plaintext message to the source.
     *
     * @param message The message.
     */
    public abstract void sendMessage(String message);
}
