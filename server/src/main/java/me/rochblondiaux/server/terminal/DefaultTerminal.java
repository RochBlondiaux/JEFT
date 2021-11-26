package me.rochblondiaux.server.terminal;

import lombok.Getter;
import me.rochblondiaux.server.Server;
import me.rochblondiaux.server.terminal.commands.Command;
import me.rochblondiaux.server.terminal.commands.CommandsManager;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Manager used to register {@link Command commands}.
 * <p>
 * It is also possible to simulate a command using.
 */
@Getter
public class DefaultTerminal implements ITerminal {

    private final String PROMPT = "> ";
    private final CommandsManager commandsManager;
    private Terminal terminal;
    private boolean running = false;

    public DefaultTerminal(CommandsManager commandsManager) {
        this.commandsManager = commandsManager;
    }

    public DefaultTerminal(Server server) {
        this.commandsManager = new CommandsManager(server);
    }

    public void start() {
        final Thread thread = new Thread(null, () -> {
            try {
                terminal = TerminalBuilder.terminal();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(commandsManager)
                    .build();
            running = true;

            while (running) {
                String command;
                try {
                    command = reader.readLine(PROMPT);
                    commandsManager.handleCommandExecution(command);
                } catch (UserInterruptException e) {
                    // Ignore
                } catch (EndOfFileException e) {
                    return;
                }
            }
        }, "Default Terminal");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
        if (terminal == null) return;
        try {
            terminal.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
