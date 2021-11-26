package me.rochblondiaux.server.terminal.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.server.utils.Pagination;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Help Menu")
@RequiredArgsConstructor
public class HelpCommand implements Command {

    private final CommandsManager manager;
    private final Pagination<Command> pagination = new Pagination<>();

    @Override
    public String getKey() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays help menu.";
    }

    @Override
    public void execute(String[] args) {
        int page = 1;
        int pages = (manager.getCommands().size() / 5) + 1;
        if (args.length >= 1 && args[0] != null) {
            try {
                page = Integer.parseInt(args[0]);
                if (page <= 0) {
                    log.warn("Please specify a number greater than 0.");
                    return;
                } else if (page > pages) {
                    log.warn("This page do not exists!");
                    return;
                }
            } catch (Exception e) {
                log.warn("Please specify a valid number.");
                return;
            }
        }
        log.info("--------- HELP ---------");
        pagination.paginate(manager.getCommands(), 5, page - 1)
                .forEach(command -> log.info(command.getKey() + "  " + command.getDescription()));
        if (pages > 1)
            log.info("Type 'help [page]' to change page.\n");
        else
            log.info("------------------------");
    }
}
