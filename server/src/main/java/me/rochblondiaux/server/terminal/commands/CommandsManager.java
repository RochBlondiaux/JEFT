package me.rochblondiaux.server.terminal.commands;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.commons.utils.Utils;
import me.rochblondiaux.server.Server;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Commands Manager")
public class CommandsManager implements Completer {

    @Getter
    private final List<Command> commands = new ArrayList<>();

    public CommandsManager(Server server) {
        register(new HelpCommand(this));
        register(new ShutdownCommand(server));
    }

    public void register(Command command) {
        this.commands.add(command);
    }

    public void handleCommandExecution(String line) {
        String[] args = line.split(" ");
        getByKey(args[0])
                .ifPresentOrElse(command -> {
                    try {
                        command.execute(Utils.removeElements(args, args[0]));
                    } catch (Exception e) {
                        log.error("Cannot execute command!", e);
                    }
                }, () -> log.warn("Unknown command! Type 'help' to display all available commands."));
    }

    public Optional<Command> getByKey(String key) {
        return commands.stream()
                .filter(command -> command.getKey().equalsIgnoreCase(key))
                .findFirst();
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String buffer = line.line();
        if (buffer == null) {
            candidates.addAll(commands.stream()
                    .map(Command::getKey)
                    .map(Candidate::new)
                    .collect(Collectors.toList()));
            return;
        }
        candidates.addAll(commands.stream()
                .map(Command::getKey)
                .filter(s -> s.startsWith(buffer))
                .map(Candidate::new)
                .collect(Collectors.toList()));
        if (candidates.size() == 1)
            candidates.set(0, new Candidate(candidates.get(0) + " "));
    }
}
