package me.rochblondiaux.client.network.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.Readable;
import me.rochblondiaux.network.netty.packets.InboundPacket;
import me.rochblondiaux.network.packets.listeners.PacketListenersManager;
import me.rochblondiaux.network.packets.server.ServerPacketIdentifier;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Packet Processor")
@RequiredArgsConstructor
public class PacketProcessor {

    private final ConnectionManager connectionManager;
    private final PacketListenersManager<NettyServerConnection> listenersManager;

    public void process(ChannelHandlerContext context, InboundPacket packet) {
        final SocketChannel socketChannel = (SocketChannel) context.channel();

        NettyServerConnection connection = connectionManager.getServer().getConnection();
        if (connection == null) {
            context.close();
            return;
        }

        // Prevent the client from sending packets when disconnected (kick)
        if (!connection.isOnline() || !socketChannel.isActive()) {
            connection.disconnect();
            return;
        }

        final int packetId = packet.getId();
        BinaryReader binaryReader = new BinaryReader(packet.getByteBuf());

        log.debug("Packet id: 0x{}", Integer.toHexString(packetId));
        log.debug("Identifying packet type...");
        ServerPacketIdentifier.getById(packetId)
                .ifPresentOrElse(packet1 -> {
                    log.debug("Packet type found! Class: {}", packet1.getClass().getName());
                    safeRead(connection, packet1, binaryReader);
                    listenersManager.onPacketReceive(connection, packet1);
                }, () -> log.error("Unknown packet received from server!"));
    }

    /**
     * Calls {@link Readable#read(BinaryReader)} and catch all the exceptions to be printed using the packet processor logger.
     *
     * @param connection the connection who sent the packet
     * @param readable   the readable interface
     * @param reader     the buffer containing the packet
     */
    private void safeRead(NettyServerConnection connection, Readable readable, BinaryReader reader) {
        final int readableBytes = reader.available();

        // Check if there is anything to read
        if (readableBytes == 0) {
            return;
        }

        try {
            readable.read(reader);
        } catch (Exception e) {
            log.warn("Connection {} sent an unexpected packet.", connection.getRemoteAddress());
            e.printStackTrace();
        }
    }

}
