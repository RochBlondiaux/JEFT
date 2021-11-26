package me.rochblondiaux.network.packets.server.play.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
@AllArgsConstructor
public class ServerSliceFailPacket implements ServerPacket {

    @Getter
    private int chunkId;

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.SLICE_FAIL;
    }

    @Override
    public void read(BinaryReader reader) {
        this.chunkId = reader.readVarInt();
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeVarInt(chunkId);
    }
}
