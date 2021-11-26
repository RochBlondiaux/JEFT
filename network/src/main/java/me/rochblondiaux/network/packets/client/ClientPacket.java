package me.rochblondiaux.network.packets.client;

import me.rochblondiaux.commons.models.network.Packet;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Represents a packet received from a client.
 */
public interface ClientPacket extends Packet {

    ClientPacketIdentifier getIdentifier();

    @Override
    default int getId() {
        return getIdentifier().getId();
    }

}