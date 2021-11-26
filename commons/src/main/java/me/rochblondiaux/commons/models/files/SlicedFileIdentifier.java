package me.rochblondiaux.commons.models.files;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.commons.utils.binary.Writeable;

import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Data
@RequiredArgsConstructor
public class SlicedFileIdentifier implements Writeable {

    private final UUID uniqueId;

    public SlicedFileIdentifier() {
        this.uniqueId = UUID.randomUUID();
    }

    public static SlicedFileIdentifier read(BinaryReader reader) {
        return new SlicedFileIdentifier(reader.readUuid());
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeUuid(uniqueId);
    }
}
