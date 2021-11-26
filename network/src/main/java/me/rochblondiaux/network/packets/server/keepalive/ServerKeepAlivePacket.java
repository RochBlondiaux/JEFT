package me.rochblondiaux.network.packets.server.keepalive;

import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.packets.server.ServerPacket;
import me.rochblondiaux.network.packets.server.ServerPacketIdentifier;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@NoArgsConstructor
public class ServerKeepAlivePacket implements ServerPacket {

    public long id;

    public ServerKeepAlivePacket(long id) {
        this.id = id;
    }

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.KEEP_ALIVE;
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeVarLong(id);
    }

    public void read(BinaryReader reader) {
        id = reader.readVarLong();
    }
}
