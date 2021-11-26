package me.rochblondiaux.network.packets.server.login;

import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.packets.server.ServerPacket;
import me.rochblondiaux.network.packets.server.ServerPacketIdentifier;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class SetCompressionPacket implements ServerPacket {

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.SET_COMPRESSION;
    }

    @Override
    public void write(BinaryWriter writer) {

    }

    @Override
    public void read(BinaryReader reader) {

    }
}
