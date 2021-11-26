package me.rochblondiaux.server.network.client;

import lombok.Getter;
import me.rochblondiaux.network.ConnectionState;
import me.rochblondiaux.network.models.client.NetworkClient;
import me.rochblondiaux.network.packets.server.ServerPacket;
import me.rochblondiaux.network.packets.server.login.LoginDisconnectPacket;
import me.rochblondiaux.network.packets.server.play.ServerDisconnectPacket;

import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
public class NettyClient implements NetworkClient {

    private final UUID uniqueId;
    private final NettyClientConnection connection;

    private long lastKeepAlive;
    private boolean answerKeepAlive;

    public NettyClient(UUID uniqueId, NettyClientConnection connection) {
        this.uniqueId = uniqueId;
        this.connection = connection;

        // Allow the server to send the next keep alive packet
        setAnswerKeepAlive(true);
    }

    @Override
    public void initializeConnection() {
        this.connection.setClient(this);
    }

    @Override
    public void disconnect() {
        disconnect("Unknown reason.");
    }

    public void disconnect(String message) {
        ServerPacket packet;
        if (connection.getConnectionState().equals(ConnectionState.LOGIN)) packet = new LoginDisconnectPacket();
        else packet = new ServerDisconnectPacket(message);
        connection.sendPacket(packet);
        connection.disconnect();
    }

    @Override
    public void setLastKeepAlive(long lastKeepAlive) {
        this.lastKeepAlive = lastKeepAlive;
        this.answerKeepAlive = false;
    }

    @Override
    public void setAnswerKeepAlive(boolean answerKeepAlive) {
        this.answerKeepAlive = answerKeepAlive;
    }

    @Override
    public boolean didAnswerKeepAlive() {
        return answerKeepAlive;
    }
}
