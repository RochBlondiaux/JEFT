package me.rochblondiaux.server.network.netty.listeners;

import me.rochblondiaux.network.packets.client.ClientPacket;
import me.rochblondiaux.network.packets.listeners.PacketListener;
import me.rochblondiaux.server.network.client.NettyClientConnection;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface ClientPacketListener<T extends ClientPacket> extends PacketListener<NettyClientConnection, T> {
}
