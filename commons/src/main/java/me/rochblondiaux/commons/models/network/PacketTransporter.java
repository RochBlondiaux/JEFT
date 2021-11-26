package me.rochblondiaux.commons.models.network;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Reponsible for {@link Packet} transport.
 */
public interface PacketTransporter<T extends Packet> {

    /**
     * Send {@link Packet} over network.
     *
     * @param packet {@link Packet} to send.
     */
    void sendPacket(T packet);

}
