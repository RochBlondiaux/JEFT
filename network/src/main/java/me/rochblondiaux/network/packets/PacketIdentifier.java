package me.rochblondiaux.network.packets;

import me.rochblondiaux.commons.models.network.Packet;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Responsible for packet identification and supplying.
 */
public interface PacketIdentifier<T extends Packet, S extends PacketSupplier<T>> {

    /**
     * Get packet identifier.
     *
     * @return packet identifier.
     */
    int getId();

    /**
     * Get packet {@link PacketSupplier}
     *
     * @return packet supplier.
     */
    S getSupplier();
}
