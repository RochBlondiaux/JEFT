package me.rochblondiaux.client.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.network.netty.PacketProcessor;
import me.rochblondiaux.commons.utils.Utils;
import me.rochblondiaux.network.utils.PacketUtils;

import java.util.List;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Packet Framer")
@RequiredArgsConstructor
public class PacketFramer extends ByteToMessageCodec<ByteBuf> {

    private final PacketProcessor packetProcessor;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf from, ByteBuf to) {
        PacketUtils.frameBuffer(from, to);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        buf.markReaderIndex();

        for (int i = 0; i < 3; ++i) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return;
            }

            final byte b = buf.readByte();

            if (b >= 0) {
                buf.resetReaderIndex();

                final int packetSize = Utils.readVarInt(buf);

                // Max packet size check
                if (packetSize >= 30000) {
                    log.warn("Server sent a packet over the maximum size ({})", packetSize);
                    ctx.close();
                }

                if (buf.readableBytes() < packetSize) {
                    buf.resetReaderIndex();
                    return;
                }

                out.add(buf.readRetainedSlice(packetSize));
                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}