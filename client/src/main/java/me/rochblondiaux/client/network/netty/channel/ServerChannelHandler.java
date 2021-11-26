package me.rochblondiaux.client.network.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.network.netty.ConnectionManager;
import me.rochblondiaux.client.network.netty.PacketProcessor;
import me.rochblondiaux.network.netty.packets.InboundPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Server Channel")
@RequiredArgsConstructor
public class ServerChannelHandler extends SimpleChannelInboundHandler<InboundPacket> {

    private final ConnectionManager connectionManager;
    private final PacketProcessor packetProcessor;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        connectionManager.createServerConnexion(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, InboundPacket packet) {
        try {
            log.debug("Packet received from server!");
            packetProcessor.process(ctx, packet);
        } catch (Exception e) {
            log.error("Cannot process packet from " + ctx.channel().remoteAddress(), e);
        } finally {
            // Check remaining
            final ByteBuf body = packet.getByteBuf();
            final int packetId = packet.getId();

            final int availableBytes = body.readableBytes();

            if (availableBytes > 0) {
                log.warn("Packet 0x{} not fully read ({} bytes left)", Integer.toHexString(packetId), availableBytes);
                body.skipBytes(availableBytes);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Disconnected!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!ctx.channel().isActive())
            return;
        cause.printStackTrace();
        ctx.close();
    }
}
