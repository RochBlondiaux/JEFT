package me.rochblondiaux.commons.models.slicer;

import lombok.NonNull;
import me.rochblondiaux.commons.models.files.SlicedFile;
import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
import me.rochblondiaux.commons.models.slices.FileSlice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * An italian implementation of {@link FileSlicer}
 * Unfortunately, it only cooks {@link FileSlice} of 1MB {@link #getChunkSize()}
 */
public class PizzaioloSlicer implements FileSlicer {

    @Override
    public SlicedFile slice(@NonNull File file) throws IOException {
        final Set<FileSlice> slices = new HashSet<>();
        final SlicedFileIdentifier identifier = new SlicedFileIdentifier();

        byte[] buffer = new byte[getChunkSize()];
        int id = 0;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int tmp;
            while ((tmp = bis.read(buffer)) > 0) {
                byte[] data = buffer;
                if (tmp < getChunkSize())
                    data = Arrays.copyOfRange(buffer, 0, tmp);
                slices.add(new FileSlice(id, identifier, data));
                buffer = new byte[getChunkSize()];
                id++;
            }
        }
        return new SlicedFile(identifier, file, slices);
    }

    @Override
    public Optional<File> unslice(@NonNull Set<FileSlice> slices) throws IOException {
        if (slices.size() == 0) return Optional.empty();
        AtomicReference<byte[]> bytes = new AtomicReference<>(new byte[]{});
        slices.stream()
                .sorted(Comparator.comparingInt(FileSlice::getId))
                .forEach(fileChunk -> bytes.set(concat(bytes.get(), fileChunk.getData())));
        Path tmpFile = Files.createTempFile("bnt", null);
        Files.write(tmpFile, bytes.get());
        return Optional.of(tmpFile.toFile());
    }

    @Override
    public int getChunkSize() {
        return 1024;
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

}
