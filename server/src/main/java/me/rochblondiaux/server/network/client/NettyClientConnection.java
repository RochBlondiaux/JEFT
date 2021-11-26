package me.rochblondiaux.server.network.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.rochblondiaux.network.ConnectionState;
import me.rochblondiaux.network.models.NetworkObjectConnection;
import me.rochblondiaux.network.netty.codec.PacketCompressor;
import me.rochblondiaux.network.netty.packets.FramedPacket;
import me.rochblondiaux.network.packets.server.ServerPacket;
import me.rochblondiaux.network.packets.server.login.SetCompressionPacket;
import me.rochblondiaux.network.utils.BufUtils;
import me.rochblondiaux.network.utils.PacketUtils;
import me.rochblondiaux.server.network.netty.NettyServerOptions;

import java.net.SocketAddress;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
@Setter
public class NettyClientConnection implements NetworkObjectConnection<ServerPacket> {


    private final SocketChannel channel;
    private final Object tickBufferLock = new Object();
    private NettyClient client;
    private volatile ConnectionState connectionState;
    private boolean online;
    @Setter
    private SocketAddress remoteAddress;
    private boolean compressed = false;
    private volatile ByteBuf tickBuffer = BufUtils.direct();

    public NettyClientConnection(@NonNull SocketChannel channel) {
        this.online = true;
        this.connectionState = ConnectionState.LOGIN;
        this.channel = channel;
        this.remoteAddress = channel.remoteAddress();
    }

    /**
     * Enables compression and add a new codec to the pipeline.
     *
     * @throws IllegalStateException if encryption is already enabled for this connection
     */
    public void startCompression() {
        if (compressed) throw new IllegalStateException("Compression is already enabled!");
        final int threshold = PacketUtils.COMPRESSION_THRESHOLD;
        if (threshold == 0)
            throw new IllegalStateException("Compression cannot be enabled because the threshold is equal to 0");
        this.compressed = true;
        writeAndFlush(new SetCompressionPacket());
        channel.pipeline().addAfter(NettyServerOptions.FRAMER_HANDLER_NAME, NettyServerOptions.COMPRESSOR_HANDLER_NAME, new PacketCompressor(threshold));
    }

    /**
     * Writes a packet to the connection channel.
     * <p>
     *
     * @param packet the packet to write
     */
    @Override
    public void sendPacket(@NonNull ServerPacket packet) {
        if (channel.isActive())
            writeAndFlush(packet);
    }

    public void write(@NonNull Object message) {
        if (message instanceof FramedPacket) {
            final FramedPacket framedPacket = (FramedPacket) message;
            synchronized (tickBufferLock) {
                if (tickBuffer.refCnt() == 0)
                    return;
                final ByteBuf body = framedPacket.getBody();
                tickBuffer.writeBytes(body, body.readerIndex(), body.readableBytes());
            }
            return;
        } else if (message instanceof ServerPacket) {
            ServerPacket packet = (ServerPacket) message;

            synchronized (tickBufferLock) {
                if (tickBuffer.refCnt() == 0)
                    return;
                PacketUtils.writeFramedPacket(tickBuffer, packet);
            }
            return;
        } else if (message instanceof ByteBuf) {
            synchronized (tickBufferLock) {
                if (tickBuffer.refCnt() == 0)
                    return;
                tickBuffer.writeBytes((ByteBuf) message);
            }
            return;
        }
        throw new UnsupportedOperationException("type " + message.getClass() + " is not supported");
    }

    public void writeAndFlush(@NonNull Object message) {
        writeWaitingPackets();
        ChannelFuture channelFuture = channel.writeAndFlush(message);

        channelFuture.addListener(future -> {
            if (!future.isSuccess() && channel.isActive())
                future.cause().printStackTrace();
        });
    }

    public void writeWaitingPackets() {
        if (tickBuffer.writerIndex() == 0)
            // Nothing to write
            return;

        // Retrieve safe copy
        final ByteBuf copy;
        synchronized (tickBufferLock) {
            if (tickBuffer.refCnt() == 0)
                return;
            copy = tickBuffer;
            tickBuffer = tickBuffer.alloc().buffer(tickBuffer.writerIndex());
        }

        // Write copied buffer to netty
        ChannelFuture channelFuture = channel.write(new FramedPacket(copy));
        channelFuture.addListener(future -> copy.release());

        // Netty debug
        channelFuture.addListener(future -> {
            if (!future.isSuccess() && channel.isActive())
                future.cause().printStackTrace();
        });
    }

    public void flush() {
        final int bufferSize = tickBuffer.writerIndex();
        if (bufferSize <= 0 || !channel.isActive()) return;
        writeWaitingPackets();
        channel.flush();
    }

    public void releaseTickBuffer() {
        synchronized (tickBufferLock) {
            tickBuffer.release();
        }
    }

    public void disconnect() {
        this.channel.close();
        this.online = false;
    }

    public boolean isConnected() {
        return online;
    }

}
