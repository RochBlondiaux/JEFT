package me.rochblondiaux.network.packets.listeners;

import me.rochblondiaux.commons.models.network.Packet;
import me.rochblondiaux.network.models.NetworkObjectConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class PacketListenersManager<N extends NetworkObjectConnection<?>> {

    private final Map<Class<? extends Packet>, PacketListener<N, ?>> listeners = new HashMap<>();

    public void register(Class<? extends Packet> clazz, PacketListener<N, ?> listener) {
        this.listeners.put(clazz, listener);
    }

    public <T extends Packet> void onPacketReceive(N connection, T packet) {
        getByClass(packet.getClass()).forEach(packetListener -> ((PacketListener<N, T>) packetListener).onReceive(connection, packet));
    }

    public <T extends Packet> List<PacketListener<N, T>> getByClass(Class<T> clazz) {
        return listeners.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(clazz))
                .map(Map.Entry::getValue)
                .map(listener -> (PacketListener<N, T>) listener)
                .collect(Collectors.toList());
    }
}
