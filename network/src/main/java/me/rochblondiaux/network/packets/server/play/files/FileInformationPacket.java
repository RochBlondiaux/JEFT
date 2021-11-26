package me.rochblondiaux.network.packets.server.play.files;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.models.files.FileInformation;
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
public class FileInformationPacket implements ServerPacket {

    @Getter
    private FileInformation file;

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.FILE_INFORMATION;
    }

    @Override
    public void write(BinaryWriter writer) {
        file.write(writer);
    }

    @Override
    public void read(BinaryReader reader) {
        this.file = FileInformation.read(reader);
    }
}
