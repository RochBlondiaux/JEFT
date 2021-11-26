package me.rochblondiaux.client.transfers;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.commons.models.files.SlicedFile;
import me.rochblondiaux.commons.models.files.SlicedFileIdentifier;
import me.rochblondiaux.commons.models.files.SlicedFileInformation;
import me.rochblondiaux.commons.models.slicer.FileSlicer;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.handler.SliceHandler;
import me.rochblondiaux.commons.models.slices.transporter.SliceTransporter;
import me.rochblondiaux.commons.models.transfers.IFileTransferManager;
import me.rochblondiaux.commons.models.transfers.IngoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.OutgoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.result.FileTransferResult;
import me.rochblondiaux.commons.models.transfers.result.FileTransferResultHandler;
import me.rochblondiaux.network.packets.client.play.transfers.ClientPreTransferPacket;
import me.rochblondiaux.network.packets.server.play.transfers.ServerSliceReceivedPacket;
import me.rochblondiaux.network.packets.server.play.transfers.ServerTransferResultPacket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Data
@Slf4j(topic = "Files Transfers Manager")
public class FileTransfersManager implements IFileTransferManager<NettyServerConnection> {

    private final SliceTransporter<NettyServerConnection> transporter;
    private final FileSlicer slicer;
    private final List<SliceHandler<NettyServerConnection>> handlers = new ArrayList<>();
    private final List<FileTransferResultHandler<NettyServerConnection>> transferResultHandler = new ArrayList<>();
    private IngoingFileTransfer<NettyServerConnection> currentIngoingTransfer;
    private OutgoingFileTransfer<NettyServerConnection> currentOutgoingTransfer;

    public void spliceAndSend(@NonNull String originalName, @NonNull File file, @NonNull NettyServerConnection connection) {
        try {
            SlicedFile slicedFile = slicer.slice(file);
            slicedFile = new SlicedFile(slicedFile.getIdentifier(), originalName, slicedFile.getFile(), slicedFile.getSlices());
            this.currentOutgoingTransfer = new OutgoingFileTransfer<>(slicedFile, connection, handlers.get(0), transporter);
            connection.sendPacket(new ClientPreTransferPacket(slicedFile.getIdentifier(), slicedFile.getInformation()));
            log.info("Pre-transfer request sent to {}!", connection.getRemoteAddress());
            currentOutgoingTransfer.sendNextSlice();
        } catch (IOException e) {
            log.error("Cannot slice file {} asked by {}!", originalName, connection.getRemoteAddress());
            e.printStackTrace();
        }
    }

    @Override
    public void handlePreTransfer(@NonNull NettyServerConnection transporter, @NonNull SlicedFileIdentifier identifier, @NonNull SlicedFileInformation information) {
        log.info("Server sent a pre-transfer request for {}.", information.getName());
        if (currentIngoingTransfer != null) {
            log.error("Pre-transfer request from server for {} decline! Cause: already uploading a file!", information.getName());
            return;
        }
        currentIngoingTransfer = new IngoingFileTransfer<>(identifier, information, transporter, handlers.get(0));
        log.info("Pre-transfer request from server for {} accepted!", information.getName());
    }

    @Override
    public void handleSliceUpload(@NonNull NettyServerConnection transporter, @NonNull FileSlice slice) {
        final SlicedFileIdentifier identifier = slice.getIdentifier();
        if (currentIngoingTransfer == null || !currentIngoingTransfer.getIdentifier().equals(slice.getIdentifier())) {
            log.error("Couldn't find any active transfer with id {}", identifier.getUniqueId().toString());
            return;
        }
        handlers.forEach(handler -> handler.handle(transporter, currentIngoingTransfer, slice));
        log.debug("Server sent slice #{} ({}/{}).", slice.getId(), currentIngoingTransfer.getSlices().size(), currentIngoingTransfer.getInformation().getSlices());
    }

    @Override
    public void handleDisconnection(UUID owner) {
        this.currentIngoingTransfer = null;
        this.currentOutgoingTransfer = null;
    }

    public void handleSliceReceived(@NonNull NettyServerConnection connection, @NonNull ServerSliceReceivedPacket packet) {
        final SlicedFileIdentifier identifier = packet.getFileIdentifier();
        log.debug("Server confirmed reception of slice #{}!", packet.getChunkId());
        if (currentOutgoingTransfer == null || !currentOutgoingTransfer.getIdentifier().equals(identifier)) {
            log.error("Couldn't find any active transfer with id {}", identifier.getUniqueId().toString());
            return;
        }
        handlers.forEach(handler -> handler.handleConfirmation(connection, currentOutgoingTransfer));
        if (!currentOutgoingTransfer.isCompleted()) return;
        transferResultHandler.forEach(resultHandler -> resultHandler.accept(currentOutgoingTransfer, FileTransferResult.SUCCESSFUL));
        currentOutgoingTransfer = null;
        log.info("Transfer #{} is done!", identifier.getUniqueId().toString());

    }

    public void handleTransferResult(@NonNull ServerTransferResultPacket packet) {
        if (currentIngoingTransfer == null || !currentIngoingTransfer.isCompleted()) return;
        transferResultHandler.forEach(resultHandler -> resultHandler.accept(currentIngoingTransfer, packet.getResult()));
        // this.currentIngoingTransfer = null;
    }

    public void registerSliceHandler(@NonNull SliceHandler<NettyServerConnection> handler) {
        this.handlers.add(handler);
    }

    public void registerTransferResultHandler(@NonNull FileTransferResultHandler<NettyServerConnection> resultHandler) {
        this.transferResultHandler.add(resultHandler);
    }

    @Override
    public SliceHandler<NettyServerConnection> getHandler() {
        return handlers.get(0);
    }
}
