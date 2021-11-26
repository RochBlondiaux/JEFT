package me.rochblondiaux.server.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.network.netty.codec.GroupedPacketHandler;
import me.rochblondiaux.network.netty.codec.PacketDecoder;
import me.rochblondiaux.network.packets.client.ClientPacket;
import me.rochblondiaux.network.packets.client.keepalive.ClientKeepAlivePacket;
import me.rochblondiaux.network.packets.client.login.LoginRequestPacket;
import me.rochblondiaux.network.packets.client.play.ClientDisconnectPacket;
import me.rochblondiaux.network.packets.client.play.files.FilesRequestPacket;
import me.rochblondiaux.network.packets.client.play.transfers.ClientPreTransferPacket;
import me.rochblondiaux.network.packets.client.play.transfers.ClientSliceReceivedPacket;
import me.rochblondiaux.network.packets.client.play.transfers.ClientSliceUploadPacket;
import me.rochblondiaux.network.packets.client.play.transfers.TransferRequestPacket;
import me.rochblondiaux.network.packets.listeners.PacketListenersManager;
import me.rochblondiaux.server.Server;
import me.rochblondiaux.server.files.FilesManager;
import me.rochblondiaux.server.network.client.NettyClientConnection;
import me.rochblondiaux.server.network.events.ClientEventType;
import me.rochblondiaux.server.network.events.EventsManager;
import me.rochblondiaux.server.network.netty.channel.ClientChannelHandler;
import me.rochblondiaux.server.network.netty.codec.PacketEncoder;
import me.rochblondiaux.server.network.netty.codec.PacketFramer;
import me.rochblondiaux.server.network.netty.listeners.ClientPacketListener;
import me.rochblondiaux.server.network.update.UpdateManager;
import me.rochblondiaux.server.transfers.FileTransferManager;
import me.rochblondiaux.server.utils.NetworkUtils;

import java.net.InetSocketAddress;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Netty Server")
@Getter
public class NettyServer {

    /* Connection */
    private final ConnectionManager connectionManager;

    /* Address */
    private final String hostname;
    private final int port;
    /* Packet Processor */
    private final PacketProcessor packetProcessor;
    /* Listeners */
    private final PacketListenersManager<NettyClientConnection> listenersManager;
    /* Update */
    private final UpdateManager updateManager;
    /* Transfers */
    private final FileTransferManager fileTransferManager;
    /* Files */
    private final FilesManager filesManager;
    /* Events */
    private final EventsManager eventsManager;
    /* Netty Component */
    private EventLoopGroup boss, worker;
    private ServerBootstrap bootstrap;
    private ServerSocketChannel serverChannel;
    /* Initialization */
    private boolean initialized;

    public NettyServer(Server server, String hostname, int port) {
        this.eventsManager = new EventsManager();
        this.connectionManager = new ConnectionManager(eventsManager);
        this.listenersManager = new PacketListenersManager<>();
        this.packetProcessor = new PacketProcessor(listenersManager);
        this.updateManager = new UpdateManager(connectionManager);
        this.filesManager = server.getFilesManager();
        this.fileTransferManager = server.getFileTransferManager();

        this.hostname = hostname;
        this.port = port;
        this.initialized = false;
    }

    /**
     * Initialize netty server socket channel, options and handlers.
     */
    public void init() {
        if (initialized) {
            log.warn("Netty server is already initialized!");
            return;
        }
        initialized = true;
        log.info("Initializing netty server...");
        log.info("Searching a suitable server socket channel provider...");
        final int workerThreadCount = 4;
        Class<? extends ServerChannel> channel;
        {
            if (Epoll.isAvailable()) {
                boss = new EpollEventLoopGroup(2);
                worker = new EpollEventLoopGroup(workerThreadCount);

                channel = EpollServerSocketChannel.class;

                log.info("Using epoll as socket channel provider!");
            } else if (KQueue.isAvailable()) {
                boss = new KQueueEventLoopGroup(2);
                worker = new KQueueEventLoopGroup(workerThreadCount);

                channel = KQueueServerSocketChannel.class;

                log.info("Using kqueue as socket channel provider!");
            } else {
                boss = new NioEventLoopGroup(2);
                worker = new NioEventLoopGroup(workerThreadCount);

                channel = NioServerSocketChannel.class;
                log.info("Using NIO as socket channel provider!");
            }
        }

        bootstrap = new ServerBootstrap()
                .group(boss, worker)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, NettyServerOptions.SERVER_WRITE_MARK)
                .channel(channel);


        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) {
                ChannelConfig config = ch.config();
                config.setOption(ChannelOption.TCP_NODELAY, true);
                config.setOption(ChannelOption.SO_KEEPALIVE, true);
                config.setOption(ChannelOption.SO_SNDBUF, 65535);
                config.setAllocator(ByteBufAllocator.DEFAULT);

                ChannelPipeline pipeline = ch.pipeline();

                // Used to bypass all the previous handlers by directly sending a framed buffer
                pipeline.addLast(NettyServerOptions.GROUPED_PACKET_HANDLER_NAME, new GroupedPacketHandler());

                // Adds packetLength at start | Reads framed buffer
                pipeline.addLast(NettyServerOptions.FRAMER_HANDLER_NAME, new PacketFramer(packetProcessor));

                // Reads buffer and create inbound packet
                pipeline.addLast(NettyServerOptions.DECODER_HANDLER_NAME, new PacketDecoder());

                // Writes packet to buffer
                pipeline.addLast(NettyServerOptions.ENCODER_HANDLER_NAME, new PacketEncoder());

                pipeline.addLast(NettyServerOptions.CLIENT_CHANNEL_NAME, new ClientChannelHandler(packetProcessor, connectionManager));
            }
        });

        registerListeners();

        log.info("Netty server successfully initialized!");
    }

    /**
     * Binds the address to start the server.
     */
    public void start() {
        log.info("Starting netty server...");
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        /*if (!address.isUnresolved()) {
            log.error("Cannot resolve address {}:{}", address.getHostString(), address.getPort());
            System.exit(0);
            return;
        } else */
        if (!NetworkUtils.isPortAvailable(address)) {
            log.error("Port {} is already in use!", address.getPort());
            System.exit(0);
            return;
        }
        // Bind address
        try {
            ChannelFuture cf = bootstrap.bind(address).sync();

            if (!cf.isSuccess())
                throw new IllegalStateException("Unable to bind server at " + hostname + ":" + port);

            /* Update */
            this.updateManager.start();

            log.info("Server successfully bind at {}:{}", hostname, port);
            this.serverChannel = (ServerSocketChannel) cf.channel();
        } catch (InterruptedException ex) {
            /* Update */
            this.updateManager.stop();
            ex.printStackTrace();
        }
    }

    /**
     * Stops the server.
     */
    public void stop() {
        log.info("Stopping netty server...");
        try {
            /* Update */
            this.updateManager.stop();
            this.connectionManager.shutdown();
            this.boss.shutdownGracefully().sync();
            this.worker.shutdownGracefully().sync();
            this.serverChannel.closeFuture().sync();
            log.info("Netty server stopped!");
        } catch (InterruptedException e) {
            log.error("Cannot stop netty server properly!", e);
        }
    }

    /**
     * Register all default packet listeners & custom events listeners.
     */
    private void registerListeners() {
        registerListener(LoginRequestPacket.class, (connection, packet) -> connectionManager.login(connection));
        registerListener(ClientDisconnectPacket.class, (connection, packet) -> connectionManager.handleDisconnection(connection));
        registerListener(ClientKeepAlivePacket.class, (ClientPacketListener<ClientKeepAlivePacket>) connectionManager::handleKeepAliveAnswer);
        registerListener(ClientPreTransferPacket.class, (ClientPacketListener<ClientPreTransferPacket>) (connection, packet) -> fileTransferManager.handlePreTransfer(connection, packet.getFileIdentifier(), packet.getInformation()));
        registerListener(ClientSliceUploadPacket.class, (ClientPacketListener<ClientSliceUploadPacket>) (connection, packet) -> fileTransferManager.handleSliceUpload(connection, packet.getSlice()));
        registerListener(TransferRequestPacket.class, (ClientPacketListener<TransferRequestPacket>) (connection, packet) -> fileTransferManager.handleTransferRequest(connection, packet, filesManager));
        registerListener(ClientSliceReceivedPacket.class, (ClientPacketListener<ClientSliceReceivedPacket>) fileTransferManager::handleSliceConfirmation);
        registerListener(FilesRequestPacket.class, (connection, packet) -> filesManager.handleFilesRequest(connection));

        /* Events */
        this.eventsManager.register(ClientEventType.CONNECT, filesManager::handleClientLogin);
        this.eventsManager.register(ClientEventType.DISCONNECT, filesManager::handleClientDisconnection);
        this.eventsManager.register(ClientEventType.DISCONNECT, connection -> fileTransferManager.handleDisconnection(connection.getClient().getUniqueId()));
    }

    /**
     * Register packet listener.
     *
     * @param listener {@link ClientPacketListener} to register.
     */
    public void registerListener(Class<? extends ClientPacket> clazz, ClientPacketListener<?> listener) {
        this.listenersManager.register(clazz, listener);
    }
}
