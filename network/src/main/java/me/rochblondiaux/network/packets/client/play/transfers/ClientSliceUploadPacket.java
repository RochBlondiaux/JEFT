package me.rochblondiaux.network.packets.client.play.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.models.slices.FileSlice;
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
public class ClientSliceUploadPacket implements ClientPlayPacket {

    @Getter
    private FileSlice slice;

    @Override
    public ClientPacketIdentifier getIdentifier() {
        return ClientPacketIdentifier.SLICE_UPLOAD;
    }

    @Override
    public void write(BinaryWriter writer) {
        slice.write(writer);
    }

    @Override
    public void read(BinaryReader reader) {
        this.slice = FileSlice.read(reader);
    }
}
