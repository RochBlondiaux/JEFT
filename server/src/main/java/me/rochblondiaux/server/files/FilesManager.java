package me.rochblondiaux.server.files;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.commons.models.files.ClientFile;
import me.rochblondiaux.commons.models.files.FileInformation;
import me.rochblondiaux.commons.models.transfers.IngoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.result.FileTransferResult;
import me.rochblondiaux.network.packets.server.play.files.FileInformationPacket;
import me.rochblondiaux.network.packets.server.play.transfers.ServerTransferResultPacket;
import me.rochblondiaux.server.network.client.NettyClientConnection;
import me.rochblondiaux.server.storage.StorageManager;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Files Manager")
public class FilesManager {

    private final List<ClientFile> cache = new ArrayList<>();
    private final StorageManager manager;
    private final File dataFolder;

    public FilesManager(StorageManager manager, File dataFolder) {
        this.manager = manager;
        this.dataFolder = new File(dataFolder, "files");
        if (!this.dataFolder.exists())
            if (!this.dataFolder.mkdir()) log.error("Cannot create data folder! Check parent folder permissions!");
    }

    /**
     * Store temporary file to its definitive location,
     * and store it as {@link ClientFile} in database.
     *
     * @param transfer
     * @param tmpFile
     */
    public void storeFile(@NonNull IngoingFileTransfer<NettyClientConnection> transfer, File tmpFile) {
        final NettyClientConnection connection = transfer.getTransporter();
        final InetAddress address = getAddress(connection);
        File clientFolder = new File(dataFolder, address.getHostAddress());
        if (!clientFolder.exists())
            if (!clientFolder.mkdir()) {
                log.error("Cannot create client folder!");
                connection.sendPacket(new ServerTransferResultPacket(FileTransferResult.UNKNOWN_ERROR));
                return;
            }
        File file = new File(clientFolder, transfer.getInformation().getName());
        try {
            Files.copy(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.delete(tmpFile.toPath());
        } catch (IOException e) {
            log.error("Cannot copy temporary file to its definitive destination!", e);
            connection.sendPacket(new ServerTransferResultPacket(FileTransferResult.UNKNOWN_ERROR));
            return;
        }
        ClientFile clientFile = new ClientFile(address, file, file.length());
        manager.insert(clientFile);
        cache.add(clientFile);
        connection.sendPacket(new FileInformationPacket(clientFile.toFileInformation()));
    }

    /**
     * Load all {@link ClientFile} of {@link NettyClientConnection} from database to cache.
     *
     * @param connection {@link NettyClientConnection} client who is login.
     */
    public void handleClientLogin(NettyClientConnection connection) {
        final InetAddress address = getAddress(connection);
        if (isCached(address)) return;
        log.info("Loading {} files from database...", connection.getRemoteAddress());
        manager.get(address)
                .thenAccept(clientFiles -> {
                    this.cache.addAll(clientFiles);
                    log.info("{} files successfully loaded from database!", connection.getRemoteAddress());
                });
    }

    /**
     * Send all client {@link ClientFile}.
     *
     * @param connection {@link NettyClientConnection} who requested its files.
     */
    public void handleFilesRequest(@NonNull NettyClientConnection connection) {
        log.debug("{} asked for its files!", connection.getRemoteAddress());
        getFromCache(connection).forEach(file -> {
            final FileInformation information = file.toFileInformation();
            log.debug("Sending {} to {}", information.getName(), connection.getRemoteAddress());
            connection.sendPacket(new FileInformationPacket(information));
        });
    }

    /**
     * Remove client's {@link ClientFile} from cache.
     *
     * @param connection {@link NettyClientConnection} client connection which is disconnecting.
     */
    public void handleClientDisconnection(NettyClientConnection connection) {
        final InetAddress address = getAddress(connection);
        cache.removeIf(file -> file.getAddress().equals(address));
    }

    public List<ClientFile> getFromCache(@NonNull NettyClientConnection connection) {
        return getFromCache(getAddress(connection));
    }

    /**
     * Get all {@link ClientFile} of {@link NettyClientConnection} from cache.
     *
     * @param address {@link NettyClientConnection} address.
     * @return cached {@link ClientFile}.
     */
    public List<ClientFile> getFromCache(@NonNull InetAddress address) {
        return cache.stream()
                .filter(file -> file.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    /**
     * Check if client {@link ClientFile} are stored in cache.
     *
     * @param address {@link NettyClientConnection} address.
     * @return true if it's the case.
     */
    public boolean isCached(@NonNull InetAddress address) {
        return cache.stream().anyMatch(file -> file.getAddress().equals(address));
    }

    private InetAddress getAddress(@NonNull NettyClientConnection connection) {
        return ((InetSocketAddress) connection.getRemoteAddress()).getAddress();
    }
}
