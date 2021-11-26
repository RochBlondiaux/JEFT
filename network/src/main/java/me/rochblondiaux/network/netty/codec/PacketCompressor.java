package me.rochblondiaux.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import me.rochblondiaux.commons.utils.Utils;
import me.rochblondiaux.network.utils.PacketUtils;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class PacketCompressor extends ByteToMessageCodec<ByteBuf> {

    private final static int MAX_SIZE = 2097152;

    private final int threshold;

    private final Deflater deflater = new Deflater();
    private final Inflater inflater = new Inflater();

    public PacketCompressor(int threshold) {
        this.threshold = threshold;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf from, ByteBuf to) {
        PacketUtils.compressBuffer(deflater, from, to);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() == 0) return;
        final int claimedUncompressedSize = Utils.readVarInt(in);

        if (claimedUncompressedSize == 0) {
            out.add(in.readRetainedSlice(in.readableBytes()));
            return;
        }
        if (claimedUncompressedSize < this.threshold)
            throw new DecoderException("Badly compressed packet - size of " + claimedUncompressedSize + " is below server threshold of " + this.threshold);

        if (claimedUncompressedSize > MAX_SIZE)
            throw new DecoderException("Badly compressed packet - size of " + claimedUncompressedSize + " is larger than protocol maximum of " + MAX_SIZE);

        // TODO optimize to do not initialize arrays each time

        byte[] input = new byte[in.readableBytes()];
        in.readBytes(input);

        inflater.setInput(input);
        byte[] output = new byte[claimedUncompressedSize];
        inflater.inflate(output);
        inflater.reset();

        out.add(Unpooled.wrappedBuffer(output));
    }
}