package me.rochblondiaux.network.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class BufUtils {

    private static final PooledByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;

    public static ByteBuf direct() {
        return alloc.ioBuffer();
    }
}