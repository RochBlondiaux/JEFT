package me.rochblondiaux.commons.models.files;

import lombok.Data;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.commons.utils.binary.Writeable;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Data
public class SlicedFileInformation implements Writeable {

    private final String name;
    private final int slices;

    public static SlicedFileInformation read(BinaryReader reader) {
        String name = reader.readSizedString();
        int chunks = reader.readVarInt();
        return new SlicedFileInformation(name, chunks);
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeSizedString(name);
        writer.writeVarInt(slices);
    }
}
