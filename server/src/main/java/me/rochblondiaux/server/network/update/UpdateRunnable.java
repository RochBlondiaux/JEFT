package me.rochblondiaux.server.network.update;

import lombok.RequiredArgsConstructor;
import me.rochblondiaux.commons.utils.StandaloneRunnable;
import me.rochblondiaux.server.network.netty.ConnectionManager;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@RequiredArgsConstructor
public class UpdateRunnable extends StandaloneRunnable {

    private final UpdateManager manager;
    private final ConnectionManager connectionManager;

    @Override
    public void run() {
        connectionManager.handleKeepAlive(System.currentTimeMillis());
    }
}
