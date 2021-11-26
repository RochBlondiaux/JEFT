package me.rochblondiaux.commons.utils;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public abstract class StandaloneRunnable implements Runnable {

    private final ScheduledExecutorService executorService;
    @Getter
    private volatile boolean running = false;

    public StandaloneRunnable() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Attempts to cancel this task.
     */
    public synchronized void cancel() {
        this.running = false;
        executorService.shutdownNow();
    }

    public synchronized void runAsync(int delay, int period, TimeUnit unit) {
        this.running = true;
        executorService.scheduleAtFixedRate(this, delay, period, unit);
    }

    public synchronized void runAsync() {
        this.running = true;
        CompletableFuture.runAsync(this);
    }

}
