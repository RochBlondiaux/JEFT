package me.rochblondiaux.network.utils;

import io.netty.buffer.ByteBuf;
import me.rochblondiaux.commons.models.network.Packet;
import me.rochblondiaux.commons.utils.Utils;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.netty.packets.InboundPacket;

import java.nio.ByteBuffer;
import java.util.zip.Deflater;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Utils class for packets. Including writing a {@link InboundPacket} into a {@link ByteBuf}
 * for network processing.
 */
public class PacketUtils {

    public static final int COMPRESSION_THRESHOLD = 256;
    private static final ThreadLocal<Deflater> COMPRESSOR = ThreadLocal.withInitial(Deflater::new);

    /**
     * Writes a {@link Packet} into a {@link ByteBuf}.
     *
     * @param buf    the recipient of {@code packet}
     * @param packet the packet to write into {@code buf}
     */
    public static void writePacket(ByteBuf buf, Packet packet) {
        Utils.writeVarInt(buf, packet.getId());
        writePacketPayload(buf, packet);
    }

    /**
     * Writes a packet payload.
     *
     * @param packet the packet to write
     */
    private static void writePacketPayload(ByteBuf buffer, Packet packet) {
        BinaryWriter writer = new BinaryWriter(buffer);
        try {
            packet.write(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Frames a buffer for it to be understood by a both connection sides.
     * <p>
     * The content of {@code packetBuffer} can be either a compressed or uncompressed packet buffer,
     * it depends on if the client did receive a SetCompressionPacket packet before.
     *
     * @param packetBuffer the buffer containing compressed or uncompressed packet data
     * @param frameTarget  the buffer which will receive the framed version of {@code from}
     */
    public static void frameBuffer(ByteBuf packetBuffer, ByteBuf frameTarget) {
        final int packetSize = packetBuffer.readableBytes();
        final int headerSize = Utils.getVarIntSize(packetSize);
        if (headerSize > 3)
            throw new IllegalStateException("Unable to fit " + headerSize + " into 3");

        frameTarget.ensureWritable(packetSize + headerSize);

        Utils.writeVarInt(frameTarget, packetSize);
        frameTarget.writeBytes(packetBuffer, packetBuffer.readerIndex(), packetSize);
    }

    /**
     * Compress using zlib the content of a packet.
     * <p>
     * {@code packetBuffer} needs to be the packet content without any header.
     *
     * @param deflater          the deflater for zlib compression
     * @param packetBuffer      the buffer containing all the packet fields
     * @param compressionTarget the buffer which will receive the compressed version of {@code packetBuffer}
     */
    public static void compressBuffer(Deflater deflater, ByteBuf packetBuffer, ByteBuf compressionTarget) {
        final int packetLength = packetBuffer.readableBytes();
        final boolean compression = packetLength > COMPRESSION_THRESHOLD;
        Utils.writeVarInt(compressionTarget, compression ? packetLength : 0);
        if (compression)
            compress(deflater, packetBuffer, compressionTarget);
        else
            compressionTarget.writeBytes(packetBuffer);
    }

    private static void compress(Deflater deflater, ByteBuf uncompressed, ByteBuf compressed) {
        deflater.setInput(uncompressed.nioBuffer());
        deflater.finish();

        while (!deflater.finished()) {
            ByteBuffer nioBuffer = compressed.nioBuffer(compressed.writerIndex(), compressed.writableBytes());
            compressed.writerIndex(deflater.deflate(nioBuffer) + compressed.writerIndex());

            if (compressed.writableBytes() == 0)
                compressed.ensureWritable(8192);
        }

        deflater.reset();
    }

    public static void writeFramedPacket(ByteBuf buffer, Packet serverPacket) {
        // Index of the var-int containing the complete packet length
        final int packetLengthIndex = Utils.writeEmpty3BytesVarInt(buffer);
        final int startIndex = buffer.writerIndex(); // Index where the content starts (after length)
        if (COMPRESSION_THRESHOLD > 0) {
            // Index of the uncompressed payload length
            final int dataLengthIndex = Utils.writeEmpty3BytesVarInt(buffer);

            // Write packet
            final int contentIndex = buffer.writerIndex();
            writePacket(buffer, serverPacket);
            final int packetSize = buffer.writerIndex() - contentIndex;

            final int uncompressedLength = packetSize >= COMPRESSION_THRESHOLD ? packetSize : 0;
            Utils.write3BytesVarInt(buffer, dataLengthIndex, uncompressedLength);
            if (uncompressedLength > 0) {
                // Packet large enough, compress
                ByteBuf uncompressedCopy = buffer.copy(contentIndex, packetSize);
                buffer.writerIndex(contentIndex);
                compress(COMPRESSOR.get(), uncompressedCopy, buffer);
                uncompressedCopy.release();
            }
        } else
            // No compression, write packet id + payload
            writePacket(buffer, serverPacket);

        // Total length
        final int totalPacketLength = buffer.writerIndex() - startIndex;
        Utils.write3BytesVarInt(buffer, packetLengthIndex, totalPacketLength);
    }

    /**
     * Creates a "framed packet" (packet which can be sent and understood by both connection sides)
     * from a server packet, directly into an output buffer.
     * <p>
     * Can be used if you want to store a raw buffer and send it later without the additional writing cost.
     * Compression is applied if compression  is greater than 0.
     */
    public static ByteBuf createFramedPacket(Packet serverPacket) {
        ByteBuf packetBuf = BufUtils.direct();
        writeFramedPacket(packetBuf, serverPacket);
        return packetBuf;
    }
}