package me.rochblondiaux.client.network.netty.listeners;

import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.network.packets.listeners.PacketListener;
import me.rochblondiaux.network.packets.server.ServerPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface ServerPacketListener<T extends ServerPacket> extends PacketListener<NettyServerConnection, T> {
}
