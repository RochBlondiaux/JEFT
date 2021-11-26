package me.rochblondiaux.network.packets.server.play.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
import me.rochblondiaux.commons.models.files.SlicedFileInformation;
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
public class ServerPreTransferPacket implements ServerPacket {

    private SlicedFileIdentifier fileIdentifier;
    private SlicedFileInformation information;

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.PRE_TRANSFER;
    }

    @Override
    public void write(BinaryWriter writer) {
        information.write(writer);
        fileIdentifier.write(writer);
    }

    @Override
    public void read(BinaryReader reader) {
        this.information = SlicedFileInformation.read(reader);
        this.fileIdentifier = SlicedFileIdentifier.read(reader);
    }

}
