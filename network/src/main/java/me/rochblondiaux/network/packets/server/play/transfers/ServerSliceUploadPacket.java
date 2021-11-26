package me.rochblondiaux.network.packets.server.play.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.models.slices.FileSlice;
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
public class ServerSliceUploadPacket implements ServerPacket {

    @Getter
    private FileSlice slice;

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.SLICE_UPLOAD;
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
