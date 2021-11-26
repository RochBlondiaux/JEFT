package me.rochblondiaux.server.network.netty;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.commons.utils.AsyncUtils;
import me.rochblondiaux.network.ConnectionState;
import me.rochblondiaux.network.packets.client.keepalive.ClientKeepAlivePacket;
import me.rochblondiaux.network.packets.server.keepalive.ServerKeepAlivePacket;
import me.rochblondiaux.network.packets.server.login.LoginSuccessPacket;
import me.rochblondiaux.network.packets.server.play.ServerDisconnectPacket;
import me.rochblondiaux.server.network.client.NettyClient;
import me.rochblondiaux.server.network.client.NettyClientConnection;
import me.rochblondiaux.server.network.events.ClientEventType;
import me.rochblondiaux.server.network.events.EventsManager;
import me.rochblondiaux.server.network.providers.ClientProvider;
import me.rochblondiaux.server.network.providers.NettyClientProvider;
import me.rochblondiaux.server.network.providers.RandomUUIDProvider;
import me.rochblondiaux.server.network.providers.UUIDProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Manages the connected clients.
 */
@Getter
@Setter
@Slf4j(topic = "Connection Manager")
public class ConnectionManager {

    private static final long KEEP_ALIVE_DELAY = 10_000;
    private static final long KEEP_ALIVE_KICK = 30_000;

    private final Set<NettyClient> clients = new CopyOnWriteArraySet<>();
    private final Map<NettyClientConnection, NettyClient> connectionClientMap = new ConcurrentHashMap<>();
    private final EventsManager eventsManager;
    private Set<NettyClient> unmodifiableClients = Collections.unmodifiableSet(clients);
    private UUIDProvider uuidProvider;
    // The client provider to have your own Client implementation
    private ClientProvider clientProvider;

    public ConnectionManager(EventsManager eventsManager) {
        this.eventsManager = eventsManager;
        this.clientProvider = new NettyClientProvider();
        this.uuidProvider = new RandomUUIDProvider();
    }

    /**
     * Gets the {@link NettyClient} linked to a {@link NettyClientConnection}.
     *
     * @param connection the client connection
     * @return the client linked to the connection
     */
    public NettyClient getClient(@NonNull NettyClientConnection connection) {
        return connectionClientMap.get(connection);
    }

    /**
     * Gets all online clients.
     *
     * @return an unmodifiable collection containing all the online clients
     */
    public Collection<NettyClient> getOnlineClients() {
        return unmodifiableClients;
    }

    /**
     * Gets the first client which validate {@link UUID#equals(Object)}.
     * <p>
     * This can cause issue if two or more clients have the same UUID.
     *
     * @param uuid the client UUID
     * @return the first client who validate the UUID condition
     */
    public Optional<NettyClient> getClient(@NonNull UUID uuid) {
        return getOnlineClients().stream()
                .filter(nettyClient -> nettyClient.getUniqueId().equals(uuid))
                .findFirst();
    }

    /**
     * Adds a new {@link NettyClient} in the clients list.
     *
     * @param client the client to add
     */
    public synchronized void registerClient(NettyClient client) {
        this.clients.add(client);
        this.connectionClientMap.put(client.getConnection(), client);
    }

    /**
     * Removes a {@link NettyClient} from the clients list.
     * <p>
     * Used during disconnection, you shouldn't have to do it manually.
     *
     * @param connection the client connection
     * @see NettyClientConnection#disconnect() to properly disconnect a client
     */
    public void removeClient(@NonNull NettyClientConnection connection) {
        final NettyClient client = this.connectionClientMap.get(connection);
        if (client == null) return;

        this.clients.remove(client);
        this.connectionClientMap.remove(connection);

        eventsManager.call(ClientEventType.DISCONNECT, connection);
    }

    public void login(@NonNull NettyClientConnection connection) {
        log.info("{} is trying to login...", connection.getRemoteAddress());
        AsyncUtils.runAsync(() -> {
            NettyClient client = clientProvider.provide(uuidProvider, connection);
            client.initializeConnection();
            connection.writeAndFlush(new LoginSuccessPacket(client.getUniqueId()));

            connection.setConnectionState(ConnectionState.CONNECTED);
            registerClient(client);
            connection.startCompression();

            eventsManager.call(ClientEventType.CONNECT, connection);
            log.info("{} successfully logged in!", connection.getRemoteAddress());
        });
    }

    /**
     * Updates keep alive by checking the last keep alive packet and send a new one if needed.
     *
     * @param tickStart the time of the update in milliseconds, forwarded to the packet
     */
    public void handleKeepAlive(long tickStart) {
        final ServerKeepAlivePacket keepAlivePacket = new ServerKeepAlivePacket(tickStart);
        getOnlineClients().forEach(client -> {
            final long lastKeepAlive = tickStart - client.getLastKeepAlive();
            if (lastKeepAlive > KEEP_ALIVE_DELAY && client.didAnswerKeepAlive()) {
                final NettyClientConnection clientConnection = client.getConnection();
                client.setLastKeepAlive(tickStart);
                clientConnection.sendPacket(keepAlivePacket);
            } else if (lastKeepAlive >= KEEP_ALIVE_KICK) {
                log.warn("{} timed out!", client.getConnection().getRemoteAddress());
                client.disconnect("Timed out!");
                connectionClientMap.remove(client.getConnection());
                clients.remove(client);
            }
        });
    }

    public void handleKeepAliveAnswer(NettyClientConnection connection, ClientKeepAlivePacket packet) {
        final NettyClient client = getClient(connection);
        if (Objects.isNull(client)) return;
        client.setAnswerKeepAlive(true);
    }

    /**
     * Handle client disconnection.
     *
     * @param connection {@link NettyClientConnection} client connection.
     */
    public void handleDisconnection(NettyClientConnection connection) {
        connectionClientMap.remove(connection);
        final NettyClient client = getClient(connection);
        if (Objects.nonNull(client))
            clients.remove(client);
        connection.disconnect();
        this.unmodifiableClients = Collections.unmodifiableSet(clients);
        log.info("{} disconnected!", connection.getRemoteAddress());
    }

    /**
     * Shutdowns the connection manager by kicking all the currently connected clients.
     */
    public void shutdown() {
        log.info("Disconnecting all connected clients...");
        ServerDisconnectPacket disconnectPacket = new ServerDisconnectPacket("Server stopped!");
        getOnlineClients().forEach(client -> {
            final Channel channel = client.getConnection().getChannel();
            channel.writeAndFlush(disconnectPacket);
            channel.close();
        });
        this.clients.clear();
        this.connectionClientMap.clear();
        log.info("Clients successfully disconnected!");
    }
}
