package me.rochblondiaux.server.terminal.commands;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface Command {

    String getKey();

    String getDescription();

    void execute(String[] args);

}
