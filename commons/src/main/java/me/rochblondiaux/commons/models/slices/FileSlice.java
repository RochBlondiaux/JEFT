package me.rochblondiaux.commons.models.slices;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.commons.utils.binary.Writeable;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Data
@RequiredArgsConstructor
public class FileSlice implements Writeable {

    private final int id;
    private final SlicedFileIdentifier identifier;
    private final byte[] data;

    public static FileSlice read(BinaryReader reader) {
        int id = reader.readVarInt();
        SlicedFileIdentifier identifier = SlicedFileIdentifier.read(reader);
        byte[] data = reader.readRemainingBytes();
        return new FileSlice(id, identifier, data);
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeVarInt(id);
        identifier.write(writer);
        writer.writeBytes(data);
    }
}
