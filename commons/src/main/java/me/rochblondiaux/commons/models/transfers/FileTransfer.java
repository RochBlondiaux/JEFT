package me.rochblondiaux.commons.models.transfers;

import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.handler.SliceHandler;

import java.util.Set;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface FileTransfer<T extends PacketTransporter<?>> {

    /**
     * Get {@link SlicedFileIdentifier} needed to identify all transfer objects.
     *
     * @return {@link SlicedFileIdentifier}
     */
    SlicedFileIdentifier getIdentifier();

    /**
     * Get {@link SliceHandler} which handles slices reception and reception confirmation.
     *
     * @return {@link SliceHandler}
     */
    SliceHandler<T> getHandler();

    /**
     * Get {@link FileSlice} set.
     *
     * @return {@link Set} of {@link FileSlice}
     */
    Set<FileSlice> getSlices();

    /**
     * Check if transfer is completed
     *
     * @return true if it's the case
     */
    boolean isCompleted();
}
