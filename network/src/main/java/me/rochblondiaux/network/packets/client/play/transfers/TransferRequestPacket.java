package me.rochblondiaux.network.packets.client.play.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.models.files.FileInformation;
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
public class TransferRequestPacket implements ClientPlayPacket {

    @Getter
    private FileInformation information;

    @Override
    public ClientPacketIdentifier getIdentifier() {
        return ClientPacketIdentifier.TRANSFER_REQUEST;
    }

    @Override
    public void write(BinaryWriter writer) {
        information.write(writer);
    }

    @Override
    public void read(BinaryReader reader) {
        this.information = FileInformation.read(reader);
    }
}
