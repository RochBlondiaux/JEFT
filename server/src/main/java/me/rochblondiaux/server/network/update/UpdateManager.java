package me.rochblondiaux.server.network.update;

import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.server.network.netty.ConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Update Manager")
public class UpdateManager {

    private final UpdateRunnable runnable;

    public UpdateManager(ConnectionManager manager) {
        this.runnable = new UpdateRunnable(this, manager);
    }

    public void start() {
        log.info("Starting update runnable...");
        if (!runnable.isRunning()) {
            this.runnable.runAsync(0, 1, TimeUnit.SECONDS);
            log.info("Update runnable started!");
            return;
        }
        log.warn("Update runnable is already running!");
    }

    public void stop() {
        log.info("Stopping update runnable...");
        if (runnable.isRunning()) {
            runnable.cancel();
            log.info("Update runnable stopped!");
            return;
        }
        log.warn("Update runnable is not running!");
    }

}
