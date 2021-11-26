package me.rochblondiaux.commons.models.slices.distributor;

import lombok.NonNull;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.transporter.SliceTransporter;
import me.rochblondiaux.commons.utils.UnEditableSet;

import java.util.Optional;
import java.util.Queue;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface SliceDistributor<T extends PacketTransporter<?>> {

    /**
     * Get next {@link FileSlice} in queue to be distributed.
     *
     * @return optional of {@link FileSlice}
     */
    Optional<FileSlice> getNextSlice();

    /**
     * Deliver the next {@link FileSlice} in queue.
     *
     * @param transporter {@link PacketTransporter} acts as a Pizza Hut delivery guy.
     */
    default void sendNextSlice(@NonNull T transporter) {
        getNextSlice().ifPresent(slice -> getTransporter().transport(transporter, slice));
        // , () -> {
        //     throw new IllegalStateException("Whole pizza got eaten. No more slices to distribute.");
        // });
    }

    /**
     * Get progress out of 100%
     *
     * @return progress
     */
    default int getProgress() {
        return (int) (((double) getQueue().size() / getSlices().size()) * 100);
    }

    /**
     * Get {@link FileSlice} queue.
     *
     * @return {@link Queue} of {@link FileSlice}
     */
    Queue<FileSlice> getQueue();

    /**
     * Get all {@link FileSlice} even if there are already delivered.
     *
     * @return {@link UnEditableSet} of {@link FileSlice}
     */
    UnEditableSet<FileSlice> getSlices();

    /**
     * Get {@link SliceTransporter}
     *
     * @return {@link SliceTransporter}
     */
    SliceTransporter<T> getTransporter();
}
