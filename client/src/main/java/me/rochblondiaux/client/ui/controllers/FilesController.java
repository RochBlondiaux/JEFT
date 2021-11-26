package me.rochblondiaux.client.ui.controllers;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.files.FilesManager;
import me.rochblondiaux.client.transfers.FileTransfersManager;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Files Controller")
public class FilesController {

    private final FilesManager filesManager;
    private final FileTransfersManager fileTransfersManager;

    @Setter
    private ViewsController viewsController;

    public FilesController(Client client) {
        this.filesManager = client.getFilesManager();
        this.fileTransfersManager = client.getFileTransfersManager();
    }


}
