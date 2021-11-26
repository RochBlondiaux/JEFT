package me.rochblondiaux.network.netty.packets;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Represents a packet which is already framed. (packet id+payload) + optional compression
 * Can be used if you want to send the exact same buffer to multiple clients without processing it more than once.
 */
@Data
public class FramedPacket {

    private final ByteBuf body;
}
