package me.rochblondiaux.server.network.events;

import lombok.NonNull;
import me.rochblondiaux.server.network.client.NettyClientConnection;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@FunctionalInterface
public interface ClientEventListener {

    void process(@NonNull NettyClientConnection connection);

}
