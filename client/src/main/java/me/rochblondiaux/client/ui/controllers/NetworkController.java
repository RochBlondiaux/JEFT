package me.rochblondiaux.client.ui.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.rochblondiaux.client.network.ConnectionResult;
import me.rochblondiaux.client.network.netty.NettyClient;
import me.rochblondiaux.client.network.server.NettyServer;
import me.rochblondiaux.network.ConnectionState;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@RequiredArgsConstructor
public class NetworkController {

    private final NettyClient client;

    public CompletableFuture<ConnectionResult> connect(@NonNull String rawHostName) {
        String[] data = rawHostName.split(":");
        String hostname = data[0];
        int port = 9999;
        if (data.length >= 2) port = Integer.parseInt(data[1]);
        return client.connect(hostname, port);
    }

    public boolean isLoggedIn() {
        final NettyServer server = client.getConnectionManager().getServer();
        return Objects.nonNull(server) && server.getConnection().getConnectionState().equals(ConnectionState.CONNECTED);
    }
}
