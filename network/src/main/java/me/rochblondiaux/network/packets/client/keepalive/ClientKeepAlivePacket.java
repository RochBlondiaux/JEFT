package me.rochblondiaux.network.packets.client.keepalive;

import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.packets.client.ClientPacketIdentifier;
import me.rochblondiaux.network.packets.client.ClientPlayPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@NoArgsConstructor
public class ClientKeepAlivePacket implements ClientPlayPacket {

    public long id;

    public ClientKeepAlivePacket(long id) {
        this.id = id;
    }

    @Override
    public ClientPacketIdentifier getIdentifier() {
        return ClientPacketIdentifier.KEEP_ALIVE;
    }

    @Override
    public void read(BinaryReader reader) {
        this.id = reader.readLong();
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeLong(id);
    }
}
