package me.rochblondiaux.network.packets.client.login;

import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.packets.client.ClientLoginPacket;
import me.rochblondiaux.network.packets.client.ClientPacketIdentifier;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class LoginRequestPacket implements ClientLoginPacket {

    @Override
    public void read(BinaryReader reader) {
    }

    @Override
    public void write(BinaryWriter writer) {
    }

    @Override
    public ClientPacketIdentifier getIdentifier() {
        return ClientPacketIdentifier.LOGIN_REQUEST;
    }

}