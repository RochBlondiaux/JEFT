package me.rochblondiaux.server.transfers;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.commons.models.files.FileInformation;
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
import me.rochblondiaux.commons.utils.AsyncUtils;
import me.rochblondiaux.network.packets.client.play.transfers.ClientSliceReceivedPacket;
import me.rochblondiaux.network.packets.client.play.transfers.TransferRequestPacket;
import me.rochblondiaux.network.packets.server.play.transfers.ServerPreTransferPacket;
import me.rochblondiaux.network.packets.server.play.transfers.ServerTransferResultPacket;
import me.rochblondiaux.server.files.FilesManager;
import me.rochblondiaux.server.network.client.NettyClientConnection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Data
@Slf4j(topic = "Files Transfers Manager")
public class FileTransferManager implements IFileTransferManager<NettyClientConnection> {

    private final Map<UUID, IngoingFileTransfer<NettyClientConnection>> ingoingTransfers = new HashMap<>();
    private final Map<UUID, OutgoingFileTransfer<NettyClientConnection>> outgoingTransfers = new HashMap<>();

    private final FilesManager filesManager;
    private final SliceTransporter<NettyClientConnection> transporter;
    private final SliceHandler<NettyClientConnection> handler;
    private final FileSlicer slicer;

    @Override
    public void handlePreTransfer(@NonNull NettyClientConnection transporter, @NonNull SlicedFileIdentifier identifier, @NonNull SlicedFileInformation information) {
        final UUID id = transporter.getClient().getUniqueId();
        log.info("{} sent a pre-transfer request for {}.", transporter.getRemoteAddress(), information.getName());
        if (ingoingTransfers.containsKey(id) && !ingoingTransfers.get(id).isCompleted()) {
            log.error("Pre-transfer request from {} for {} decline! Cause: already uploading a file!", transporter.getRemoteAddress(), information.getName());
            return;
        }
        ingoingTransfers.put(id, new IngoingFileTransfer<>(identifier, information, transporter, handler));
        log.info("Pre-transfer request from {} for {} accepted!", transporter.getRemoteAddress(), information.getName());
    }

    @Override
    public void handleSliceUpload(@NonNull NettyClientConnection transporter, @NonNull FileSlice slice) {
        final SlicedFileIdentifier identifier = slice.getIdentifier();
        getInGoingByIdentifier(identifier)
                .ifPresentOrElse(ft -> {
                    log.debug("File slice #{} of {} received from {}", slice.getId(), ft.getInformation().getName(), transporter.getRemoteAddress());
                    handler.handle(transporter, ft, slice);
                    if (!ft.isCompleted()) return;
                    ingoingTransfers.remove(transporter.getClient().getUniqueId());
                    AsyncUtils.runAsync(() -> {
                        log.info("Unslicing file...");
                        try {
                            slicer.unslice(ft.getSlices())
                                    .ifPresentOrElse(file -> {
                                        filesManager.storeFile(ft, file);
                                        log.info("File unsliced!");
                                        transporter.sendPacket(new ServerTransferResultPacket(FileTransferResult.SUCCESSFUL));
                                        log.info("Transfer #{} is done!", identifier.getUniqueId().toString());
                                    }, () -> transporter.sendPacket(new ServerTransferResultPacket(FileTransferResult.SLICING_ERROR)));
                        } catch (IOException e) {
                            transporter.sendPacket(new ServerTransferResultPacket(FileTransferResult.SLICING_ERROR));
                            log.error("Cannot unslice file!", e);
                        }
                    });
                }, () -> log.error("Couldn't find any active transfer with id {}", identifier.getUniqueId().toString()));
    }

    public void handleTransferRequest(@NonNull NettyClientConnection connection, @NonNull TransferRequestPacket packet, @NonNull FilesManager filesManager) {
        final FileInformation information = packet.getInformation();
        log.info("{} asked for {}", connection.getRemoteAddress(), information.getName());
        filesManager.getFromCache(connection)
                .stream()
                .filter(clientFile -> clientFile.getUniqueId().equals(information.getUniqueId()))
                .findFirst()
                .ifPresentOrElse(clientFile -> {
                    log.info("Sending {} to {}...", information.getName(), connection.getRemoteAddress());
                    final File file = clientFile.getFile();
                    try {
                        SlicedFile slicedFile = slicer.slice(file);
                        OutgoingFileTransfer<NettyClientConnection> transfer = new OutgoingFileTransfer<>(slicedFile, connection, handler, transporter);
                        outgoingTransfers.put(connection.getClient().getUniqueId(), transfer);
                        connection.sendPacket(new ServerPreTransferPacket(slicedFile.getIdentifier(), slicedFile.getInformation()));
                        log.info("Pre-transfer request sent to {}!", connection.getRemoteAddress());
                        transfer.sendNextSlice();
                    } catch (IOException e) {
                        log.error("Cannot slice file {} asked by {}!", information.getName(), connection.getRemoteAddress());
                        e.printStackTrace();
                    }
                }, () -> log.error("Cannot find {} asked by {}!", information.getName(), connection.getRemoteAddress()));
    }

    public void handleSliceConfirmation(@NonNull NettyClientConnection connection, @NonNull ClientSliceReceivedPacket packet) {
        log.debug("{} confirmed slice #{}!", connection.getRemoteAddress(), packet.getChunkId());
        final SlicedFileIdentifier identifier = packet.getFileIdentifier();
        getOutGoingByIdentifier(packet.getFileIdentifier())
                .ifPresentOrElse(transfer -> {
                    handler.handleConfirmation(connection, transfer);
                    if (!transfer.isCompleted()) return;
                    connection.sendPacket(new ServerTransferResultPacket(FileTransferResult.SUCCESSFUL));
                    outgoingTransfers.remove(connection.getClient().getUniqueId());
                }, () -> {
                    // connection.sendPacket(new ServerTransferResultPacket(FileTransferResult.UNKNOWN_ERROR));
                    log.error("Couldn't find any active transfer with id {}", identifier.getUniqueId().toString());
                });
    }

    @Override
    public void handleDisconnection(UUID owner) {
        ingoingTransfers.remove(owner);
        outgoingTransfers.remove(owner);
    }

    public Optional<OutgoingFileTransfer<NettyClientConnection>> getOutGoingByIdentifier(@NonNull SlicedFileIdentifier identifier) {
        return outgoingTransfers.values()
                .stream()
                .filter(fileTransfer -> fileTransfer.getIdentifier().getUniqueId().equals(identifier.getUniqueId()))
                .findFirst();
    }

    public Optional<IngoingFileTransfer<NettyClientConnection>> getInGoingByIdentifier(@NonNull SlicedFileIdentifier identifier) {
        return ingoingTransfers.values()
                .stream()
                .filter(fileTransfer -> fileTransfer.getIdentifier().getUniqueId().equals(identifier.getUniqueId()))
                .findFirst();
    }
}
