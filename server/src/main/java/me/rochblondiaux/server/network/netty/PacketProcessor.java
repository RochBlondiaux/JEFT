package me.rochblondiaux.server.network.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.Readable;
import me.rochblondiaux.network.ConnectionState;
import me.rochblondiaux.network.netty.packets.InboundPacket;
import me.rochblondiaux.network.packets.client.ClientPacketIdentifier;
import me.rochblondiaux.network.packets.listeners.PacketListenersManager;
import me.rochblondiaux.server.network.client.NettyClientConnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Responsible for processing client packets.
 * <p>
 * You can retrieve the different packet handlers per state (login/play)
 * from the {@link ClientPacketIdentifier} class.
 * <p>
 * Packet handlers are cached here and can be retrieved with {@link ClientPacketIdentifier}
 * The one to use depend on the type of packet you need to retrieve.
 */
@Slf4j(topic = "Packet Processor")
@RequiredArgsConstructor
public final class PacketProcessor {

    private final Map<ChannelHandlerContext, NettyClientConnection> connectionClientConnectionMap = new ConcurrentHashMap<>();
    private final PacketListenersManager<NettyClientConnection> listenersManager;

    public void process(ChannelHandlerContext context, InboundPacket packet) {
        final SocketChannel socketChannel = (SocketChannel) context.channel();

        // Create the netty client connection object if not existing
        NettyClientConnection clientConnection = connectionClientConnectionMap.get(context);
        if (clientConnection == null) {
            // Should never happen
            context.close();
            return;
        }

        log.debug("Packet received from {}", clientConnection.getRemoteAddress());

        // Prevent the client from sending packets when disconnected (kick)
        if (!clientConnection.isOnline() || !socketChannel.isActive()) {
            clientConnection.disconnect();
            return;
        }

        final ConnectionState connectionState = clientConnection.getConnectionState();
        if (connectionState.equals(ConnectionState.UNKNOWN)) {
            log.error("Unknown connection state for {}", clientConnection.getRemoteAddress());
            return;
        }

        final int packetId = packet.getId();
        BinaryReader binaryReader = new BinaryReader(packet.getByteBuf());

        log.debug("Packet id: 0x{}", Integer.toHexString(packetId));
        log.debug("Identifying packet type...");
        ClientPacketIdentifier.getById(packetId)
                .ifPresentOrElse(packet1 -> {
                    log.debug("Packet type found! Class: {}", packet1.getClass().getName());
                    safeRead(clientConnection, packet1, binaryReader);
                    listenersManager.onPacketReceive(clientConnection, packet1);
                }, () -> log.error("Unknown packet received from {}!", clientConnection.getRemoteAddress()));
    }

    /**
     * Retrieves a client connection from its channel.
     *
     * @param context the connection context
     * @return the connection of this channel, null if not found
     */
    public NettyClientConnection getClientConnection(ChannelHandlerContext context) {
        return connectionClientConnectionMap.get(context);
    }

    public void createClientConnection(ChannelHandlerContext context) {
        final NettyClientConnection clientConnection = new NettyClientConnection((SocketChannel) context.channel());
        connectionClientConnectionMap.put(context, clientConnection);
    }

    public NettyClientConnection removeClientConnection(ChannelHandlerContext context) {
        return connectionClientConnectionMap.remove(context);
    }

    /**
     * Calls {@link Readable#read(BinaryReader)} and catch all the exceptions to be printed using the packet processor logger.
     *
     * @param connection the connection who sent the packet
     * @param readable   the readable interface
     * @param reader     the buffer containing the packet
     */
    private void safeRead(NettyClientConnection connection, Readable readable, BinaryReader reader) {
        final int readableBytes = reader.available();

        // Check if there is anything to read
        if (readableBytes == 0)
            return;

        try {
            readable.read(reader);
        } catch (Exception e) {
            log.warn("Connection {} sent an unexpected packet.", connection.getRemoteAddress());
            e.printStackTrace();
        }
    }
}