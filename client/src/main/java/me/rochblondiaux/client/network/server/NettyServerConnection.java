package me.rochblondiaux.client.network.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.rochblondiaux.client.network.netty.NettyClientOptions;
import me.rochblondiaux.network.ConnectionState;
import me.rochblondiaux.network.models.NetworkObjectConnection;
import me.rochblondiaux.network.netty.codec.PacketCompressor;
import me.rochblondiaux.network.packets.client.ClientPacket;
import me.rochblondiaux.network.utils.PacketUtils;

import java.net.SocketAddress;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
@Setter
public class NettyServerConnection implements NetworkObjectConnection<ClientPacket> {

    private final SocketChannel channel;
    private NettyServer client;
    private volatile ConnectionState connectionState;
    private boolean online;
    @Setter
    private SocketAddress remoteAddress;

    private boolean compressed = false;

    public NettyServerConnection(@NonNull SocketChannel channel) {
        this.online = true;
        this.connectionState = ConnectionState.UNKNOWN;
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
        channel.pipeline().addAfter(NettyClientOptions.FRAMER_HANDLER_NAME, NettyClientOptions.COMPRESSOR_HANDLER_NAME, new PacketCompressor(threshold));
    }


    /**
     * Writes a packet to the connection channel.
     *
     * @param packet the packet to write
     */
    @Override
    public void sendPacket(@NonNull ClientPacket packet) {
        if (channel.isActive())
            writeAndFlush(packet);
    }


    public void writeAndFlush(@NonNull Object message) {
        ChannelFuture channelFuture = channel.writeAndFlush(message);

        channelFuture.addListener(future -> {
            if (!future.isSuccess() && channel.isActive())
                future.cause().printStackTrace();
        });
    }

    public void disconnect() {
        this.connectionState = ConnectionState.UNKNOWN;
        this.online = false;
        this.channel.close();
    }

    @Override
    public void write(@NonNull Object message) {
        // unused
    }

    @Override
    public void writeWaitingPackets() {
        // unused
    }

    @Override
    public void flush() {
        // unused
    }

    @Override
    public void releaseTickBuffer() {
        // unused
    }

}
