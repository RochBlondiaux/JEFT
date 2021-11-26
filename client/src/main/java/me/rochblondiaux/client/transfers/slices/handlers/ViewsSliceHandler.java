package me.rochblondiaux.client.transfers.slices.handlers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.client.ui.controllers.ViewsController;
import me.rochblondiaux.client.ui.views.LoadingView;
import me.rochblondiaux.client.utils.ImageUtils;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.handler.SliceHandler;
import me.rochblondiaux.commons.models.transfers.FileTransfer;
import me.rochblondiaux.commons.models.transfers.IngoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.OutgoingFileTransfer;

import javax.swing.*;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@RequiredArgsConstructor
public class ViewsSliceHandler implements SliceHandler<NettyServerConnection> {

    private final ViewsController controller;

    @Override
    public void handle(@NonNull NettyServerConnection transporter, @NonNull IngoingFileTransfer<NettyServerConnection> transfer, @NonNull FileSlice slice) {
        update(transfer, (int) (((double) transfer.getSlices().size() / transfer.getInformation().getSlices()) * 100));
    }

    @Override
    public void handleConfirmation(@NonNull NettyServerConnection transporter, @NonNull OutgoingFileTransfer<NettyServerConnection> transfer) {
        update(transfer, 100 - transfer.getDistributor().getProgress());
    }

    private void update(FileTransfer<NettyServerConnection> transfer, int progress) {
        if (!(controller.getCurrentView() instanceof LoadingView)) {
            if (transfer instanceof OutgoingFileTransfer)
                controller.setCurrentView(new LoadingView(Client.get(), "Uploading...", "uploading"));
            else
                controller.setCurrentView(new LoadingView(Client.get(), "Downloading...", "downloading"));
        }
        final LoadingView view = (LoadingView) controller.getCurrentView();
        view.getProgressBar().setValue(progress);
        if (!transfer.isCompleted() || progress >= 100) return;
        view.getProgressBar().setVisible(false);
        view.getLabel().setText("Processing...");
        view.getLabel().setIcon(new ImageIcon(ImageUtils.getResourcesURL("/loaders/rocket.gif")));
    }
}
