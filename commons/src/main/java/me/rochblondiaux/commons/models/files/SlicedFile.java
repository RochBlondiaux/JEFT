package me.rochblondiaux.commons.models.files;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.commons.utils.binary.Writeable;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Data
@RequiredArgsConstructor
public class SlicedFile implements Writeable {

    private final SlicedFileIdentifier identifier;
    private final SlicedFileInformation information;
    private final File file;
    private final Set<FileSlice> slices;

    public SlicedFile(SlicedFileIdentifier identifier, File file, Set<FileSlice> slices) {
        this.identifier = identifier;
        this.file = file;
        this.slices = slices;
        this.information = new SlicedFileInformation(file.getName(), slices.size());
    }

    public SlicedFile(SlicedFileIdentifier identifier, String finename, File file, Set<FileSlice> slices) {
        this.identifier = identifier;
        this.file = file;
        this.slices = slices;
        this.information = new SlicedFileInformation(finename, slices.size());
    }

    public static SlicedFile read(BinaryReader reader) {
        SlicedFileIdentifier identifier = SlicedFileIdentifier.read(reader);
        SlicedFileInformation information = SlicedFileInformation.read(reader);
        return new SlicedFile(identifier, information, null, new HashSet<>());
    }

    @Override
    public void write(BinaryWriter writer) {
        identifier.write(writer);
        information.write(writer);
    }
}
