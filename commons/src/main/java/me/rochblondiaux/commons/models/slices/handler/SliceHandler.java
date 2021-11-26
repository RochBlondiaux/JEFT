package me.rochblondiaux.commons.models.slices.handler;

import lombok.NonNull;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.transfers.IngoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.OutgoingFileTransfer;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Reponsible for {@link FileSlice} reception and receiption confirmation.
 */
public interface SliceHandler<T extends PacketTransporter<?>> {

    /**
     * Handle {@link FileSlice} reception.
     *
     * @param transporter {@link PacketTransporter} sender.
     * @param slice       {@link FileSlice} to handle.
     * @param transfer    {@link OutgoingFileTransfer}
     */
    void handle(@NonNull T transporter, @NonNull IngoingFileTransfer<T> transfer, @NonNull FileSlice slice);

    /**
     * Handle {@link FileSlice} reception confirmation.
     *
     * @param transporter {@link PacketTransporter} who confirmed {@link FileSlice} reception.
     * @param transfer    {@link OutgoingFileTransfer}
     */
    void handleConfirmation(@NonNull T transporter, @NonNull OutgoingFileTransfer<T> transfer);
}
