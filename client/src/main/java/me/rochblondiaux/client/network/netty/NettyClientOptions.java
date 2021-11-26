package me.rochblondiaux.client.network.netty;

import io.netty.channel.WriteBufferWaterMark;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class NettyClientOptions {

    public static final WriteBufferWaterMark SERVER_WRITE_MARK = new WriteBufferWaterMark(1 << 20, 1 << 21);

    public static final String FRAMER_HANDLER_NAME = "framer"; // Read/write

    public static final String COMPRESSOR_HANDLER_NAME = "compressor"; // Read/write

    public static final String DECODER_HANDLER_NAME = "decoder"; // Read
    public static final String ENCODER_HANDLER_NAME = "encoder"; // Write
    public static final String SERVER_CHANNEL_NAME = "handler"; // Read
}
