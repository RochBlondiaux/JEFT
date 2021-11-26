package me.rochblondiaux.commons.models.transfers;

import lombok.NonNull;
import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
import me.rochblondiaux.commons.models.files.SlicedFileInformation;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.handler.SliceHandler;
import me.rochblondiaux.commons.models.slices.transporter.SliceTransporter;

import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface IFileTransferManager<T extends PacketTransporter<?>> {


    /**
     * Handle pre-transfer packet.
     *
     * @param transporter {@link PacketTransporter} who sent the packet.
     * @param identifier  {@link SlicedFileIdentifier} file identifier.
     * @param information {@link SlicedFileInformation} which needs to be handled.
     */
    void handlePreTransfer(@NonNull T transporter, @NonNull SlicedFileIdentifier identifier, @NonNull SlicedFileInformation information);

    /**
     * Handle received {@link FileSlice} from {@link PacketTransporter}.
     *
     * @param transporter {@link PacketTransporter} who sent the packet.
     * @param slice       {@link FileSlice} which needs to be handled.
     */
    void handleSliceUpload(@NonNull T transporter, @NonNull FileSlice slice);

    /**
     * Handle network disconnection
     *
     * @param owner {@link OutgoingFileTransfer}'s owner
     */
    void handleDisconnection(UUID owner);

    SliceTransporter<T> getTransporter();

    SliceHandler<T> getHandler();
}
