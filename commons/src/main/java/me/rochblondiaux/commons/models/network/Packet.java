package me.rochblondiaux.commons.models.network;

import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.Readable;
import me.rochblondiaux.commons.utils.binary.Writeable;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface Packet extends Readable, Writeable {

    @Override
    default void read(BinaryReader reader) {
        throw new UnsupportedOperationException("WIP: This packet is not set up to be read from the code at the moment.");
    }

    /**
     * Gets the id of this packet.
     * <p>
     * Written in the final buffer header so it needs to match the client id.
     *
     * @return the id of this packet
     */
    int getId();

}