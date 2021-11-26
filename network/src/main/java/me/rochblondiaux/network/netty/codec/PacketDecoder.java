package me.rochblondiaux.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.rochblondiaux.commons.utils.Utils;
import me.rochblondiaux.network.netty.packets.InboundPacket;

import java.util.List;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) {
        if (buf.readableBytes() <= 0) return;
        final int packetId = Utils.readVarInt(buf);
        list.add(new InboundPacket(packetId, buf));
    }
}