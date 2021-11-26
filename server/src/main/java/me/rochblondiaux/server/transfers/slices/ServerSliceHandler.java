package me.rochblondiaux.server.transfers.slices;

import lombok.NonNull;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.handler.SliceHandler;
import me.rochblondiaux.commons.models.transfers.IngoingFileTransfer;
import me.rochblondiaux.commons.models.transfers.OutgoingFileTransfer;
import me.rochblondiaux.network.packets.server.play.transfers.ServerSliceReceivedPacket;
import me.rochblondiaux.server.network.client.NettyClientConnection;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ServerSliceHandler implements SliceHandler<NettyClientConnection> {

    @Override
    public void handle(@NonNull NettyClientConnection transporter, @NonNull IngoingFileTransfer<NettyClientConnection> transfer, @NonNull FileSlice slice) {
        transfer.getSlices().add(slice);
        transporter.sendPacket(new ServerSliceReceivedPacket(slice.getIdentifier(), slice.getId()));
    }

    @Override
    public void handleConfirmation(@NonNull NettyClientConnection transporter, @NonNull OutgoingFileTransfer<NettyClientConnection> transfer) {
        transfer.sendNextSlice();
    }
}
