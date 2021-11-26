package me.rochblondiaux.network.packets.server.play.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
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
@Getter
public class ServerSliceReceivedPacket implements ServerPacket {

    private SlicedFileIdentifier fileIdentifier;
    private int chunkId;

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.SLICE_RECEIVED;
    }

    @Override
    public void read(BinaryReader reader) {
        this.chunkId = reader.readVarInt();
        this.fileIdentifier = SlicedFileIdentifier.read(reader);
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeVarInt(chunkId);
        fileIdentifier.write(writer);
    }
}
