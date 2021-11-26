package me.rochblondiaux.client.transfers.slices;

import lombok.NonNull;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.transporter.SliceTransporter;
import me.rochblondiaux.network.packets.client.play.transfers.ClientSliceUploadPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ClientSliceTransporter implements SliceTransporter<NettyServerConnection> {

    @Override
    public void transport(@NonNull NettyServerConnection transporter, @NonNull FileSlice slice) {
        transporter.sendPacket(new ClientSliceUploadPacket(slice));
    }
}
