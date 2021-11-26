package me.rochblondiaux.server.transfers.slices;

import lombok.NonNull;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.transporter.SliceTransporter;
import me.rochblondiaux.network.packets.server.play.transfers.ServerSliceUploadPacket;
import me.rochblondiaux.server.network.client.NettyClientConnection;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ServerSliceTransporter implements SliceTransporter<NettyClientConnection> {

    @Override
    public void transport(@NonNull NettyClientConnection transporter, @NonNull FileSlice slice) {
        transporter.sendPacket(new ServerSliceUploadPacket(slice));
    }
}
