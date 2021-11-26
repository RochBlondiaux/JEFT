package me.rochblondiaux.client.network.server;

import lombok.Getter;
import me.rochblondiaux.network.packets.client.play.ClientDisconnectPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
public class NettyServer {

    private final NettyServerConnection connection;

    public NettyServer(NettyServerConnection connection) {
        this.connection = connection;
    }

    /**
     * Used to initialize the client connection
     */
    public void initConnection() {
        this.connection.setClient(this);
    }

    public void disconnect() {
        connection.sendPacket(new ClientDisconnectPacket());
        connection.disconnect();
    }
}
