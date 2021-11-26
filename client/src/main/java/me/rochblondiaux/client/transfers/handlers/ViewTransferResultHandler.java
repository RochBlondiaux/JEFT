package me.rochblondiaux.client.transfers.handlers;

import lombok.NonNull;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.client.ui.controllers.ViewsController;
import me.rochblondiaux.client.ui.views.IconView;
import me.rochblondiaux.commons.models.transfers.FileTransfer;
import me.rochblondiaux.commons.models.transfers.IngoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.OutgoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.result.FileTransferResult;
import me.rochblondiaux.commons.models.transfers.result.FileTransferResultHandler;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ViewTransferResultHandler implements FileTransferResultHandler<NettyServerConnection> {

    private final Client client;
    private final ViewsController controller;

    public ViewTransferResultHandler(Client client) {
        this.client = client;
        this.controller = client.getViewsController();
    }

    @Override
    public void accept(@NonNull FileTransfer<NettyServerConnection> transfer, @NonNull FileTransferResult result) {
        switch (result) {
            case SUCCESSFUL:
                if (transfer instanceof OutgoingFileTransfer)
                    controller.setCurrentView(new IconView(client, "Success", "uploaded", "/icons/check.png"));
                break;
            case ENCRYPTION_ERROR:
            case DECRYPTION_ERROR:
            case SLICING_ERROR:
            case MISSING_CHUNK:
            case UNKNOWN_ERROR:
                String msg = "upload.failed";
                if (transfer instanceof IngoingFileTransfer) msg = "download.failed";
                controller.setCurrentView(new IconView(client, "Failure", msg));
                break;
        }
    }
}
