package me.rochblondiaux.server.terminal.commands;

import lombok.RequiredArgsConstructor;
import me.rochblondiaux.server.Server;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@RequiredArgsConstructor
public class ShutdownCommand implements Command {

    private final Server server;

    @Override
    public String getKey() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stop the server.";
    }

    @Override
    public void execute(String[] args) {
        server.shutdown();
    }
}
