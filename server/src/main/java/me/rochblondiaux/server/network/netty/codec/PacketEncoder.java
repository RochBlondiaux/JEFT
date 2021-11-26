package me.rochblondiaux.server.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.rochblondiaux.network.packets.server.ServerPacket;
import me.rochblondiaux.network.utils.PacketUtils;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class PacketEncoder extends MessageToByteEncoder<ServerPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerPacket packet, ByteBuf buf) {
        PacketUtils.writePacket(buf, packet);
    }


}