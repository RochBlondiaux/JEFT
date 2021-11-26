package me.rochblondiaux.network.packets.server.play.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.models.transfers.result.FileTransferResult;
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
public class ServerTransferResultPacket implements ServerPacket {

    @Getter
    private FileTransferResult result;

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.TRANSFER_RESULT;
    }

    @Override
    public void read(BinaryReader reader) {
        this.result = FileTransferResult.valueOf(reader.readSizedString());
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeSizedString(result.name());
    }
}
