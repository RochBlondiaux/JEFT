package me.rochblondiaux.client.files;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.client.transfers.FileTransfersManager;
import me.rochblondiaux.commons.models.files.FileInformation;
import me.rochblondiaux.commons.utils.AsyncUtils;
import me.rochblondiaux.encryption.SecurityManager;
import me.rochblondiaux.network.packets.client.play.files.FilesRequestPacket;
import me.rochblondiaux.network.packets.client.play.transfers.TransferRequestPacket;
import me.rochblondiaux.network.packets.server.play.files.FileInformationPacket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Files Manager")
@RequiredArgsConstructor
public class FilesManager {

    @Getter
    private final List<FileInformation> files = new ArrayList<>();
    private final SecurityManager securityManager;
    private final FileTransfersManager transfersManager;

    /**
     * Ask server to send a file.
     *
     * @param name filename
     */
    public void downloadFile(@NonNull String name) {
        files.stream()
                .filter(fileInformation -> fileInformation.getName().equals(name))
                .findFirst()
                .ifPresentOrElse(fileInformation -> {
                    log.info("Asking server to send {}...", name);
                    getServerConnection().sendPacket(new TransferRequestPacket(fileInformation));
                }, () -> log.error("File {} don't exists in server!", name)); // This should never happen.
    }

    /**
     * This method encrypt, split file and send first chunk to server.
     * The method is run asynchronously.
     *
     * @param file {@link File} to upload.
     */
    public void uploadFile(File file) {
        AsyncUtils.runAsync(() -> {
            File tmpFile;
            try {
                tmpFile = File.createTempFile("bnc", null);
            } catch (IOException e) {
                log.error("Cannot create temporary file!", e);
                return;
            }
            try {
                securityManager.encrypt(file, tmpFile);
            } catch (IOException e) {
                log.error("Cannot encrypt file!", e);
                return;
            }
            log.info("Sending {} to server...", file.getName());
            transfersManager.spliceAndSend(file.getName(), tmpFile, getServerConnection());
        });
    }

    /**
     * Request client files to server.
     */
    public void requestFiles() {
        log.debug("Requesting files to server...");
        files.clear();
        getServerConnection().sendPacket(new FilesRequestPacket());
    }

    /**
     * Cache received file information.
     *
     * @param packet which contains file information.
     */
    public void handleFileInformation(@NonNull FileInformationPacket packet) {
        log.debug("New file information received from server!");
        this.files.add(packet.getFile());
    }

    private NettyServerConnection getServerConnection() {
        return Client.get().getNettyClient().getConnectionManager().getServer().getConnection();
    }
}
