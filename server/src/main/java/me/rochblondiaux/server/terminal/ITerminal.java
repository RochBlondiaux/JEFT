package me.rochblondiaux.server.terminal;

import me.rochblondiaux.server.terminal.commands.CommandsManager;
import org.jline.terminal.Terminal;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface ITerminal {

    void start();

    void stop();

    Terminal getTerminal();

    boolean isRunning();

    CommandsManager getCommandsManager();
}
