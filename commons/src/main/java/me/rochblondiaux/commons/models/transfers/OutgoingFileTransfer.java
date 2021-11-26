package me.rochblondiaux.commons.models.transfers;

import lombok.Getter;
import me.rochblondiaux.commons.models.files.SlicedFile;
import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.distributor.EnzoDistributor;
import me.rochblondiaux.commons.models.slices.distributor.SliceDistributor;
import me.rochblondiaux.commons.models.slices.handler.SliceHandler;
import me.rochblondiaux.commons.models.slices.transporter.SliceTransporter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
public class OutgoingFileTransfer<T extends PacketTransporter<?>> implements FileTransfer<T> {

    private final SlicedFileIdentifier identifier;
    private final SlicedFile file;
    private final Set<FileSlice> slices;
    private final T transporter;
    private final SliceDistributor<T> distributor;
    private final SliceHandler<T> handler;

    public OutgoingFileTransfer(SlicedFile file, T transporter, SliceHandler<T> handler, SliceTransporter<T> sliceTransporter) {
        this.file = file;
        this.transporter = transporter;
        this.handler = handler;
        this.identifier = file.getIdentifier();
        this.slices = new HashSet<>(file.getSlices());
        this.distributor = new EnzoDistributor<T>(sliceTransporter, this.slices);
    }

    public void sendNextSlice() {
        this.distributor.sendNextSlice(transporter);
    }

    public boolean isCompleted() {
        return distributor.getQueue().size() == 0;
    }


}
