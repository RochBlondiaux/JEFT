package me.rochblondiaux.client.transfers.handlers;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.client.ui.controllers.ViewsController;
import me.rochblondiaux.client.ui.views.IconView;
import me.rochblondiaux.client.ui.views.MainView;
import me.rochblondiaux.commons.models.slicer.FileSlicer;
import me.rochblondiaux.commons.models.transfers.FileTransfer;
import me.rochblondiaux.commons.models.transfers.IngoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.OutgoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.result.FileTransferResult;
import me.rochblondiaux.commons.models.transfers.result.FileTransferResultHandler;
import me.rochblondiaux.encryption.SecurityManager;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j
public class ClientTransferResultHandler implements FileTransferResultHandler<NettyServerConnection> {

    private final Client client;
    private final ViewsController controller;
    private final FileSlicer slicer;
    private final SecurityManager securityManager;

    public ClientTransferResultHandler(Client client) {
        this.client = client;
        this.controller = client.getViewsController();
        this.slicer = client.getFileTransfersManager().getSlicer();
        this.securityManager = client.getSecurityManager();
    }

    @Override
    public void accept(@NonNull FileTransfer<NettyServerConnection> tf, @NonNull FileTransferResult result) {
        if (!result.equals(FileTransferResult.SUCCESSFUL) || tf instanceof OutgoingFileTransfer) return;
        IngoingFileTransfer<NettyServerConnection> transfer = (IngoingFileTransfer<NettyServerConnection>) tf;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int jResult = fileChooser.showOpenDialog(controller.getCurrentView());
        if (jResult == JFileChooser.APPROVE_OPTION) {
            File destination = new File(fileChooser.getSelectedFile(), transfer.getInformation().getName());
            try {
                slicer.unslice(transfer.getSlices())
                        .ifPresentOrElse(file -> {
                            try {
                                securityManager.decrypt(file, destination);
                                controller.setCurrentView(new IconView(client, "Success", "downloaded", "/icons/check.png"));
                                FileUtils.forceDelete(file);
                            } catch (IOException e) {
                                log.error("Cannot decrypt downloaded file!", e);
                                controller.setCurrentView(new IconView(client, "Error", "decryption.fail"));
                            }
                        }, () -> {
                            log.error("Cannot reassemble slices to file!");
                            controller.setCurrentView(new IconView(client, "Error", "download.failed"));
                        });
            } catch (IOException e) {
                log.error("Cannot reassemble slices to file!", e);
                controller.setCurrentView(new IconView(client, "Error", "download.failed"));
            }
        } else
            controller.setCurrentView(MainView.class);
    }
}
