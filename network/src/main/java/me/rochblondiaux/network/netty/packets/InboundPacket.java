package me.rochblondiaux.network.netty.packets;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Data
public class InboundPacket {

    private final int id;
    private final ByteBuf byteBuf;

}
