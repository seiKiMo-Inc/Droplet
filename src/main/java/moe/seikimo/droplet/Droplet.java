package moe.seikimo.droplet;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import moe.seikimo.droplet.utils.Log;
import moe.seikimo.droplet.utils.objects.Config;
import moe.seikimo.droplet.utils.objects.Config.ConfigType;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;

public final class Droplet {
    private static final long startTime = System.currentTimeMillis();

    @Getter private static final Logger logger
            = LoggerFactory.getLogger(Droplet.class);

    @Getter private static Config environment;
    @Getter private static boolean isDebug = false;
    private static LineReader lineReader = null;

    static {
        // Declare Logback configuration.
        System.setProperty("logback.configurationFile", "logback.xml");
    }

    public static void main(String[] args) {
        Droplet.getLogger().info("Starting Droplet...");

        // Parse environment variables.
        Droplet.loadEnvironment();

        // Initialize the game server.
        Server.initialize();

        // Start the console.
        new Thread(Droplet::startConsole).start();

        // Start the game server.
        Server.getInstance().start();

        Droplet.getLogger().info("Done! Droplet started in {}ms.",
                System.currentTimeMillis() - Droplet.startTime);
    }

    /**
     * Loads environment variables.
     */
    private static void loadEnvironment() {
        var env = Droplet.environment = Config.read(
                System.getenv(), ConfigType.PROPERTIES);

        // Set the debug mode.
        Droplet.isDebug = env.getBoolean("DROPLET_DEBUG", false);
        Log.setDebug(Droplet.getLogger());
    }

    /**
     * @return The terminal line reader.
     *         Creates a new line reader if not already created.
     */
    public static LineReader getConsole() {
        // Check if the line reader exists.
        if (Droplet.lineReader == null) {
            Terminal terminal = null; try {
                // Create a native terminal.
                terminal = TerminalBuilder.builder()
                        .jna(true).build();
            } catch (Exception ignored) {
                try {
                    // Fallback to a dumb JLine terminal.
                    terminal = TerminalBuilder.builder()
                            .dumb(true).build();
                } catch (Exception ignored1) { }
            }

            // Set the line reader.
            Droplet.lineReader = LineReaderBuilder.builder()
                    .terminal(terminal).build();
        }

        return Droplet.lineReader;
    }

    /**
     * Starts the line reader.
     */
    public static void startConsole() {
        String input = null;
        var isLastInterrupted = false;
        var logger = Droplet.getLogger();

        while (true) {
            try {
                input = Droplet.getConsole().readLine("> ");
            } catch (UserInterruptException ignored) {
                if (!isLastInterrupted) {
                    isLastInterrupted = true;
                    logger.info("Press Ctrl-C again to shutdown.");
                    continue;
                } else {
                    Runtime.getRuntime().exit(0);
                }
            } catch (EndOfFileException ignored) {
                continue;
            } catch (IOError e) {
                logger.error("An IO error occurred while trying to read from console.", e);
                return;
            }

            isLastInterrupted = false;

            try {
                // Invoke the command.
                Server.getInstance().executeCommand(input);
            } catch (CommandSyntaxException ex) {
                logger.error(ex.getMessage());
            } catch (Exception ex) {
                logger.warn("An error occurred while trying to invoke command.", ex);
            }
        }
    }
}
