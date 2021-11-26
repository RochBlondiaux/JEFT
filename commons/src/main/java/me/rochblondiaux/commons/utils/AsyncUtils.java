package me.rochblondiaux.commons.utils;

import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public final class AsyncUtils {

    public static CompletableFuture<Void> runAsync(@NonNull Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}