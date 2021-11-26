package me.rochblondiaux.commons.models.transfers.result;

import lombok.NonNull;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.commons.models.transfers.FileTransfer;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Responsible for handling {@link FileTransferResult} after {@link FileTransfer} completion or failure.
 */
@FunctionalInterface
public interface FileTransferResultHandler<T extends PacketTransporter<?>> {

    void accept(@NonNull FileTransfer<T> transfer, @NonNull FileTransferResult result);
}
