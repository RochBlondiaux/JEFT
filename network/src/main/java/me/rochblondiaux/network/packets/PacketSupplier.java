package me.rochblondiaux.network.packets;

import me.rochblondiaux.commons.models.network.Packet;
import me.rochblondiaux.network.packets.client.ClientPacket;
import me.rochblondiaux.network.packets.server.ServerPacket;

import java.util.function.Supplier;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Convenient interface to supply {@link Packet} implementations.
 */
public interface PacketSupplier<T extends Packet> extends Supplier<T> {

    /**
     * Convenient interface to supply a {@link ClientPacket}.
     */
    interface ClientPacketSupplier extends PacketSupplier<ClientPacket> {
    }

    /**
     * Convenient interface to supply a {@link ServerPacket}.
     */
    interface ServerPacketSupplier extends PacketSupplier<ServerPacket> {
    }
}
