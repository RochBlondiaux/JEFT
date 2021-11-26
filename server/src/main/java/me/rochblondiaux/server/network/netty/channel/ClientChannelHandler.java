package me.rochblondiaux.server.network.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.network.netty.packets.InboundPacket;
import me.rochblondiaux.server.network.client.NettyClient;
import me.rochblondiaux.server.network.client.NettyClientConnection;
import me.rochblondiaux.server.network.netty.ConnectionManager;
import me.rochblondiaux.server.network.netty.PacketProcessor;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Client Channel Handler")
@RequiredArgsConstructor
public class ClientChannelHandler extends SimpleChannelInboundHandler<InboundPacket> {

    private final PacketProcessor packetProcessor;
    private final ConnectionManager connectionManager;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        packetProcessor.createClientConnection(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, InboundPacket packet) {
        try {
            packetProcessor.process(ctx, packet);
        } catch (Exception e) {
            log.error("Cannot process packet from " + ctx.channel().remoteAddress(), e);
        } finally {
            // Check remaining
            final ByteBuf body = packet.getByteBuf();
            final int packetId = packet.getId();

            final int availableBytes = body.readableBytes();

            if (availableBytes > 0) {
                final NettyClientConnection clientConnection = packetProcessor.getClientConnection(ctx);

                log.warn("Packet 0x{} not fully read ({} bytes left), {}", Integer.toHexString(packetId), availableBytes, clientConnection);
                body.skipBytes(availableBytes);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        NettyClientConnection clientConnection = packetProcessor.removeClientConnection(ctx);
        if (clientConnection == null) return;
        // Remove the connection
        clientConnection.setOnline(false);
        NettyClient client = clientConnection.getClient();
        if (client != null)
            connectionManager.removeClient(clientConnection);
        clientConnection.releaseTickBuffer();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!ctx.channel().isActive())
            return;
        cause.printStackTrace();
        ctx.close();
    }
}
