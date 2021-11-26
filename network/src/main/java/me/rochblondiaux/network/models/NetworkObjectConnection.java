package me.rochblondiaux.network.models;

import lombok.NonNull;
import me.rochblondiaux.commons.models.network.Packet;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.network.ConnectionState;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Represents a networking connection with Netty.
 * <p>
 * It is the implementation used for all network client & server.
 */
public interface NetworkObjectConnection<T extends Packet> extends PacketTransporter<T> {

    /**
     * Enables compression and add a new codec to the pipeline.
     *
     * @throws IllegalStateException if encryption is already enabled for this connection
     */
    void startCompression();

    /**
     * Writes a packet to the connection channel.
     * <p>
     * All packets are flushed during object update
     *
     * @param packet the packet to write
     */
    @Override
    void sendPacket(@NonNull T packet);

    /**
     * Write {@link T}, {@link me.rochblondiaux.network.netty.packets.FramedPacket}
     * or {@link io.netty.buffer.ByteBuf} to tick buffer.
     *
     * @param message object to write.
     */
    void write(@NonNull Object message);

    /**
     * Write and flush {@link T}, {@link me.rochblondiaux.network.netty.packets.FramedPacket}
     * or {@link io.netty.buffer.ByteBuf} to tick buffer then to the netty channel.
     * <p>
     * {@link #writeWaitingPackets()}
     *
     * @param message object to write and flush.
     */
    void writeAndFlush(@NonNull Object message);

    /**
     * Write waiting packets to tick buffer then to the netty channel.
     */
    void writeWaitingPackets();

    /**
     * Flush waiting packets to network
     * {@link #writeWaitingPackets()}
     */
    void flush();

    /**
     * Release tick buffer
     */
    void releaseTickBuffer();

    /**
     * Disconnect object from network.
     */
    void disconnect();

    /**
     * Gets the object connection state.
     *
     * @return the object connection state
     */
    ConnectionState getConnectionState();

    /**
     * Set the object connection state.
     *
     * @param connectionState the new object connection state
     */
    void setConnectionState(ConnectionState connectionState);
}
