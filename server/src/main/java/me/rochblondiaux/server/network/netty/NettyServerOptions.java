package me.rochblondiaux.server.network.netty;

import io.netty.channel.WriteBufferWaterMark;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class NettyServerOptions {

    public static final WriteBufferWaterMark SERVER_WRITE_MARK = new WriteBufferWaterMark(1 << 20, 1 << 21);

    public static final String ENCRYPT_HANDLER_NAME = "encrypt"; // Write
    public static final String DECRYPT_HANDLER_NAME = "decrypt"; // Read

    public static final String GROUPED_PACKET_HANDLER_NAME = "grouped-packet"; // Write
    public static final String FRAMER_HANDLER_NAME = "framer"; // Read/write

    public static final String COMPRESSOR_HANDLER_NAME = "compressor"; // Read/write

    public static final String DECODER_HANDLER_NAME = "decoder"; // Read
    public static final String ENCODER_HANDLER_NAME = "encoder"; // Write
    public static final String CLIENT_CHANNEL_NAME = "handler"; // Read
}
