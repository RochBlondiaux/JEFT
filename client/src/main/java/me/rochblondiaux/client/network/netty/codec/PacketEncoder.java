package me.rochblondiaux.client.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.rochblondiaux.network.packets.client.ClientPacket;
import me.rochblondiaux.network.utils.PacketUtils;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class PacketEncoder extends MessageToByteEncoder<ClientPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ClientPacket packet, ByteBuf buf) {
        PacketUtils.writePacket(buf, packet);
    }


}