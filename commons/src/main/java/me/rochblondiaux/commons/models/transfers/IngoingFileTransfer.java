package me.rochblondiaux.commons.models.transfers;

import lombok.Getter;
import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
import me.rochblondiaux.commons.models.files.SlicedFileInformation;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.handler.SliceHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
public class IngoingFileTransfer<T extends PacketTransporter<?>> implements FileTransfer<T> {

    private final SlicedFileIdentifier identifier;
    private final SlicedFileInformation information;
    private final Set<FileSlice> slices;
    private final T transporter;
    private final SliceHandler<T> handler;

    public IngoingFileTransfer(SlicedFileIdentifier identifier, SlicedFileInformation information, T transporter, SliceHandler<T> handler) {
        this.identifier = identifier;
        this.information = information;
        this.transporter = transporter;
        this.handler = handler;
        this.slices = new HashSet<>();
    }

    @Override
    public boolean isCompleted() {
        return slices.size() == information.getSlices();
    }
}
