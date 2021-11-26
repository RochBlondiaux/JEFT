package me.rochblondiaux.network.packets.listeners;

import me.rochblondiaux.commons.models.network.Packet;
import me.rochblondiaux.network.models.NetworkObjectConnection;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface PacketListener<N extends NetworkObjectConnection<?>, T extends Packet> {

    void onReceive(N connection, T packet);

}
