package me.rochblondiaux.commons.models.files;

import lombok.Data;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.commons.utils.binary.Writeable;

import java.util.Date;
import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Data
public class FileInformation implements Writeable {

    private final UUID uniqueId;
    private final String name;
    private final long size;
    private final Date uploadDate;

    public static FileInformation read(BinaryReader reader) {
        UUID uuid = reader.readUuid();
        String name = reader.readSizedString();
        long size = reader.readVarLong();
        Date date = reader.readDate();
        return new FileInformation(uuid, name, size, date);
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeUuid(uniqueId);
        writer.writeSizedString(name);
        writer.writeVarLong(size);
        writer.writeDate(uploadDate);
    }
}
