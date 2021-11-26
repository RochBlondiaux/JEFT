package me.rochblondiaux.commons.models.slicer;

import lombok.NonNull;
import me.rochblondiaux.commons.models.files.SlicedFile;
import me.rochblondiaux.commons.models.slices.FileSlice;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Set;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Responsible for slicing and unslicing files
 * into multiples {@link FileSlice}.
 * Chunks are easier and lightweight to send over network.
 */
public interface FileSlicer {

    /**
     * Slice file into multiple {@link FileSlice}
     *
     * @param file original {@link File}
     * @return {@link SlicedFile} sliced file
     */
    SlicedFile slice(@NonNull File file) throws IOException;

    /**
     * Re-assemble file from chunk.
     *
     * @param slices {@link FileSlice} set.
     * @return optional of {@link File} as {@link OutputStream}
     */
    Optional<File> unslice(@NonNull Set<FileSlice> slices) throws IOException;

    /**
     * Get {@link FileSlice} size
     * <p>
     * The size define on how many chunks files
     * will be divided.
     *
     * @return {@link FileSlice} size.
     */
    int getChunkSize();
}
