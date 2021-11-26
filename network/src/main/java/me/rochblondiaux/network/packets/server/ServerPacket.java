package me.rochblondiaux.network.packets.server;

import me.rochblondiaux.commons.models.network.Packet;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Represents a packet which can be sent to a client.
 */
public interface ServerPacket extends Packet {

    ServerPacketIdentifier getIdentifier();

    @Override
    default int getId() {
        return getIdentifier().getId();
    }
}