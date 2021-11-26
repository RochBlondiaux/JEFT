package me.rochblondiaux.network.packets.client.play.files;

import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.packets.client.ClientPacketIdentifier;
import me.rochblondiaux.network.packets.client.ClientPlayPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class FilesRequestPacket implements ClientPlayPacket {

    @Override
    public ClientPacketIdentifier getIdentifier() {
        return ClientPacketIdentifier.FILES_REQUEST;
    }

    @Override
    public void write(BinaryWriter writer) {
    }

    @Override
    public void read(BinaryReader reader) {

    }
}
