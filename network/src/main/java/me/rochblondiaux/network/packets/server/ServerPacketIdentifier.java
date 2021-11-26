package me.rochblondiaux.network.packets.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.rochblondiaux.network.packets.PacketIdentifier;
import me.rochblondiaux.network.packets.PacketSupplier;
import me.rochblondiaux.network.packets.server.keepalive.ServerKeepAlivePacket;
import me.rochblondiaux.network.packets.server.login.LoginDisconnectPacket;
import me.rochblondiaux.network.packets.server.login.LoginSuccessPacket;
import me.rochblondiaux.network.packets.server.login.SetCompressionPacket;
import me.rochblondiaux.network.packets.server.play.ServerDisconnectPacket;
import me.rochblondiaux.network.packets.server.play.files.FileInformationPacket;
import me.rochblondiaux.network.packets.server.play.transfers.*;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@RequiredArgsConstructor
@Getter
public enum ServerPacketIdentifier implements PacketIdentifier<ServerPacket, PacketSupplier.ServerPacketSupplier> {
    LOGIN_SUCCESS(0x00, LoginSuccessPacket::new),
    LOGIN_DISCONNECT(0x00, LoginDisconnectPacket::new),
    SET_COMPRESSION(0x02, SetCompressionPacket::new),
    KEEP_ALIVE(0x03, ServerKeepAlivePacket::new),
    DISCONNECT(0x04, ServerDisconnectPacket::new),
    SLICE_RECEIVED(0x05, ServerSliceReceivedPacket::new),
    SLICE_FAIL(0x05, ServerSliceFailPacket::new),
    TRANSFER_RESULT(0x06, ServerTransferResultPacket::new),
    FILE_INFORMATION(0x07, FileInformationPacket::new),
    SLICE_UPLOAD(0x08, ServerSliceUploadPacket::new),
    PRE_TRANSFER(0x09, ServerPreTransferPacket::new),
    ;

    private final int id;
    private final PacketSupplier.ServerPacketSupplier supplier;

    /**
     * Get {@link ServerPacket} by its identifier.
     *
     * @param id packet identifier.
     * @return {@link Optional} of packet.
     */
    public static Optional<ServerPacket> getById(int id) {
        return Arrays.stream(values())
                .filter(t -> t.getId() == id)
                .findFirst()
                .map(t -> t.getSupplier().get());
    }
}
