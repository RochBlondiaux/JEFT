package me.rochblondiaux.client.network.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.network.server.NettyServer;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.client.ui.views.ConnexionView;
import me.rochblondiaux.client.ui.views.IconView;
import me.rochblondiaux.network.ConnectionState;
import me.rochblondiaux.network.packets.client.keepalive.ClientKeepAlivePacket;
import me.rochblondiaux.network.packets.client.login.LoginRequestPacket;
import me.rochblondiaux.network.packets.server.play.ServerDisconnectPacket;


/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Connection Manager")
public class ConnectionManager {

    @Getter
    private NettyServer server;

    public void createServerConnexion(ChannelHandlerContext context) {
        this.server = new NettyServer(new NettyServerConnection((SocketChannel) context.channel()));
        this.server.initConnection();
        sendLoginRequest();
    }

    public void sendLoginRequest() {
        log.info("Connecting to server...");
        log.debug("Sending login request to server...");
        server.getConnection().sendPacket(new LoginRequestPacket());
        log.debug("Login request sent!");
    }

    public void startCompression() {
        server.getConnection().startCompression();
        log.info("Compression started!");
    }

    public void loginSuccessful() {
        server.getConnection().setConnectionState(ConnectionState.CONNECTED);
        log.info("Successfully logged in!");
    }

    public void loginFailed() {
        server.getConnection().setConnectionState(ConnectionState.UNKNOWN);
        log.warn("Cannot log in!");
    }

    public void handleDisconnection(ServerDisconnectPacket packet) {
        server.getConnection().disconnect();
        server = null;
        Client.get().getViewsController().setCurrentView(new IconView(Client.get(), "Disconnected", "Disconnected from server.", "/icons/unplugged.png", ConnexionView.class));
        log.info("Disconnected from server!");
    }

    public void handleKeepAlive() {
        server.getConnection().sendPacket(new ClientKeepAlivePacket(System.currentTimeMillis()));
    }
}
