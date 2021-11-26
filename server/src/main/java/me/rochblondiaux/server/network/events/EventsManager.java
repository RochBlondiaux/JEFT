package me.rochblondiaux.server.network.events;

import lombok.NonNull;
import me.rochblondiaux.server.network.client.NettyClientConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class EventsManager {

    private final Map<ClientEventType, List<ClientEventListener>> events = new HashMap<>();

    public void register(ClientEventType type, ClientEventListener event) {
        this.events.putIfAbsent(type, new ArrayList<>());
        this.events.get(type).add(event);
    }

    public void call(@NonNull ClientEventType type, @NonNull NettyClientConnection connection) {
        this.events.getOrDefault(type, new ArrayList<>())
                .forEach(clientEventListener -> clientEventListener.process(connection));
    }
}
