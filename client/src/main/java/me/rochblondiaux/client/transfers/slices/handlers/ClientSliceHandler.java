package me.rochblondiaux.client.transfers.slices.handlers;

import lombok.NonNull;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.handler.SliceHandler;
import me.rochblondiaux.commons.models.transfers.IngoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.OutgoingFileTransfer;
import me.rochblondiaux.network.packets.client.play.transfers.ClientSliceReceivedPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ClientSliceHandler implements SliceHandler<NettyServerConnection> {

    @Override
    public void handle(@NonNull NettyServerConnection transporter, @NonNull IngoingFileTransfer<NettyServerConnection> transfer, @NonNull FileSlice slice) {
        transfer.getSlices().add(slice);
        transporter.sendPacket(new ClientSliceReceivedPacket(slice.getIdentifier(), slice.getId()));
    }

    @Override
    public void handleConfirmation(@NonNull NettyServerConnection transporter, @NonNull OutgoingFileTransfer<NettyServerConnection> transfer) {
        transfer.sendNextSlice();
    }
}
