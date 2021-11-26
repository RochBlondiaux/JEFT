package me.rochblondiaux.network.packets.client.play;

import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.packets.client.ClientPacketIdentifier;
import me.rochblondiaux.network.packets.client.ClientPlayPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ClientDisconnectPacket implements ClientPlayPacket {

    @Override
    public ClientPacketIdentifier getIdentifier() {
        return ClientPacketIdentifier.DISCONNECT;
    }

    @Override
    public void write(BinaryWriter writer) {

    }

    @Override
    public void read(BinaryReader reader) {

    }
}
