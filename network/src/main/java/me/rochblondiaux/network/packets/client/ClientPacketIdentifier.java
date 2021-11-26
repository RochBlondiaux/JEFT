package me.rochblondiaux.network.packets.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.rochblondiaux.network.packets.PacketIdentifier;
import me.rochblondiaux.network.packets.PacketSupplier;
import me.rochblondiaux.network.packets.client.keepalive.ClientKeepAlivePacket;
import me.rochblondiaux.network.packets.client.login.LoginRequestPacket;
import me.rochblondiaux.network.packets.client.play.ClientDisconnectPacket;
import me.rochblondiaux.network.packets.client.play.files.FilesRequestPacket;
import me.rochblondiaux.network.packets.client.play.transfers.ClientPreTransferPacket;
import me.rochblondiaux.network.packets.client.play.transfers.ClientSliceReceivedPacket;
import me.rochblondiaux.network.packets.client.play.transfers.ClientSliceUploadPacket;
import me.rochblondiaux.network.packets.client.play.transfers.TransferRequestPacket;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@RequiredArgsConstructor
@Getter
public enum ClientPacketIdentifier implements PacketIdentifier<ClientPacket, PacketSupplier.ClientPacketSupplier> {
    LOGIN_REQUEST(0x00, LoginRequestPacket::new),
    KEEP_ALIVE(0x01, ClientKeepAlivePacket::new),
    DISCONNECT(0x02, ClientDisconnectPacket::new),
    PRE_TRANSFER(0x03, ClientPreTransferPacket::new),
    SLICE_UPLOAD(0x04, ClientSliceUploadPacket::new),
    FILES_REQUEST(0x05, FilesRequestPacket::new),
    TRANSFER_REQUEST(0x06, TransferRequestPacket::new),
    SLICE_RECEIVED(0x07, ClientSliceReceivedPacket::new);

    private final int id;
    private final PacketSupplier.ClientPacketSupplier supplier;

    /**
     * Get {@link ClientPacket} by its identifier.
     *
     * @param id packet identifier.
     * @return {@link Optional} of packet.
     */
    public static Optional<ClientPacket> getById(int id) {
        return Arrays.stream(values())
                .filter(t -> t.getId() == id)
                .findFirst()
                .map(t -> t.getSupplier().get());
    }
}
