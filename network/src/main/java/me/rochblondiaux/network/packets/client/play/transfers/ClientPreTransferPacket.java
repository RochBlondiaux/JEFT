package me.rochblondiaux.network.packets.client.play.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
import me.rochblondiaux.commons.models.files.SlicedFileInformation;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.packets.client.ClientPacketIdentifier;
import me.rochblondiaux.network.packets.client.ClientPlayPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClientPreTransferPacket implements ClientPlayPacket {

    private SlicedFileIdentifier fileIdentifier;
    private SlicedFileInformation information;

    @Override
    public ClientPacketIdentifier getIdentifier() {
        return ClientPacketIdentifier.PRE_TRANSFER;
    }

    @Override
    public void write(BinaryWriter writer) {
        fileIdentifier.write(writer);
        information.write(writer);
    }

    @Override
    public void read(BinaryReader reader) {
        this.fileIdentifier = SlicedFileIdentifier.read(reader);
        this.information = SlicedFileInformation.read(reader);
    }

}
