package me.rochblondiaux.client.network.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.files.FilesManager;
import me.rochblondiaux.client.network.ConnectionResult;
import me.rochblondiaux.client.network.netty.channel.ServerChannelHandler;
import me.rochblondiaux.client.network.netty.codec.PacketEncoder;
import me.rochblondiaux.client.network.netty.codec.PacketFramer;
import me.rochblondiaux.client.network.netty.listeners.ServerPacketListener;
import me.rochblondiaux.client.network.server.NettyServerConnection;
import me.rochblondiaux.client.transfers.FileTransfersManager;
import me.rochblondiaux.commons.utils.AsyncUtils;
import me.rochblondiaux.network.netty.codec.PacketDecoder;
import me.rochblondiaux.network.packets.listeners.PacketListenersManager;
import me.rochblondiaux.network.packets.server.ServerPacket;
import me.rochblondiaux.network.packets.server.keepalive.ServerKeepAlivePacket;
import me.rochblondiaux.network.packets.server.login.LoginDisconnectPacket;
import me.rochblondiaux.network.packets.server.login.LoginSuccessPacket;
import me.rochblondiaux.network.packets.server.login.SetCompressionPacket;
import me.rochblondiaux.network.packets.server.play.ServerDisconnectPacket;
import me.rochblondiaux.network.packets.server.play.files.FileInformationPacket;
import me.rochblondiaux.network.packets.server.play.transfers.ServerPreTransferPacket;
import me.rochblondiaux.network.packets.server.play.transfers.ServerSliceReceivedPacket;
import me.rochblondiaux.network.packets.server.play.transfers.ServerSliceUploadPacket;
import me.rochblondiaux.network.packets.server.play.transfers.ServerTransferResultPacket;

import java.util.concurrent.CompletableFuture;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Netty Server")
@Getter
public class NettyClient {

    /* Connection */
    private final ConnectionManager connectionManager;
    /* Packet Processor */
    private final PacketProcessor packetProcessor;
    /* Packets Listeners */
    private final PacketListenersManager<NettyServerConnection> listenersManager;
    /* File Transfers */
    private final FileTransfersManager fileTransfersManager;
    /* Files */
    private final FilesManager filesManager;
    /* Address */
    private String hostname;
    private int port;
    /* Netty Component */
    private EventLoopGroup boss;
    private Bootstrap bootstrap;
    private SocketChannel clientChannel;
    /* Initialization */
    private boolean initialized;

    public NettyClient(Client client) {
        this.fileTransfersManager = client.getFileTransfersManager();
        this.filesManager = client.getFilesManager();
        this.connectionManager = new ConnectionManager();
        this.listenersManager = new PacketListenersManager<>();
        this.packetProcessor = new PacketProcessor(connectionManager, listenersManager);
        this.initialized = false;
    }

    /**
     * Initialize netty client socket channel, options and handlers.
     */
    public void init() {
        if (initialized) {
            log.warn("Netty client is already initialized!");
            return;
        }
        initialized = true;
        log.info("Initializing netty client...");
        log.info("Searching a suitable socket channel provider...");
        Class<? extends SocketChannel> channel;
        {
            if (Epoll.isAvailable()) {
                boss = new EpollEventLoopGroup(2);

                channel = EpollSocketChannel.class;

                log.info("Using epoll as socket channel provider!");
            } else if (KQueue.isAvailable()) {
                boss = new KQueueEventLoopGroup(2);

                channel = KQueueSocketChannel.class;

                log.info("Using kqueue as socket channel provider!");
            } else {
                boss = new NioEventLoopGroup(2);

                channel = NioSocketChannel.class;
                log.info("Using NIO as socket channel provider!");
            }
        }

        bootstrap = new Bootstrap()
                .group(boss)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, NettyClientOptions.SERVER_WRITE_MARK)
                .channel(channel);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) {
                ChannelConfig config = ch.config();
                config.setOption(ChannelOption.TCP_NODELAY, true);
                config.setOption(ChannelOption.SO_KEEPALIVE, true);
                config.setOption(ChannelOption.SO_SNDBUF, 65535);
                config.setAllocator(ByteBufAllocator.DEFAULT);

                ChannelPipeline pipeline = ch.pipeline();

                // Adds packetLength at start | Reads framed buffer
                pipeline.addLast(NettyClientOptions.FRAMER_HANDLER_NAME, new PacketFramer(packetProcessor));

                // Reads buffer and create inbound packet
                pipeline.addLast(NettyClientOptions.DECODER_HANDLER_NAME, new PacketDecoder());

                // Writes packet to buffer
                pipeline.addLast(NettyClientOptions.ENCODER_HANDLER_NAME, new PacketEncoder());

                pipeline.addLast(NettyClientOptions.SERVER_CHANNEL_NAME, new ServerChannelHandler(connectionManager, packetProcessor));
            }
        });

        registerListeners();

        log.info("Netty client successfully initialized!");
    }

    /**
     * Binds the address to start the server.
     *
     * @return {@link ConnectionResult}
     */
    public CompletableFuture<ConnectionResult> connect(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        CompletableFuture<ConnectionResult> result = new CompletableFuture<>();
        AsyncUtils.runAsync(() -> {
            log.info("Connecting to netty server...");
            try {
                bootstrap.remoteAddress(hostname, port);
                ChannelFuture channelFuture = bootstrap.connect().sync();

                if (!channelFuture.isSuccess()) {
                    log.error("Unable to connect to {}:{}", hostname, port);
                    result.complete(ConnectionResult.UNKNOWN_ERROR);
                }
                log.info("Successfully connected to {}:{}", hostname, port);
                this.clientChannel = (SocketChannel) channelFuture.channel();
                result.complete(ConnectionResult.SUCCESSFUL);
            } catch (InterruptedException e) {
                log.error("Cannot connect to netty server", e.getCause());
                result.complete(ConnectionResult.UNKNOWN_ERROR);
            } catch (Exception e) {
                log.error("Cannot connect to netty server", e.getCause());
                result.complete(ConnectionResult.CONNECTION_REFUSED);
            }
        });
        return result;
    }

    /**
     * Stops the server.
     */
    public void stop() {
        log.info("Stopping netty server...");
        try {
            if (connectionManager.getServer() != null)
                connectionManager.getServer().disconnect();
            this.boss.shutdownGracefully().sync();
            this.clientChannel.closeFuture().sync();
            log.info("Netty server stopped!");
        } catch (InterruptedException e) {
            log.error("Cannot stop netty server properly!", e);
        }
    }

    /**
     * Register all defaults {@link ServerPacketListener}
     */
    private void registerListeners() {
        registerListener(LoginSuccessPacket.class, (connection, packet) -> connectionManager.loginSuccessful());
        registerListener(LoginDisconnectPacket.class, (connection, packet) -> connectionManager.loginFailed());
        registerListener(SetCompressionPacket.class, (connection, packet) -> connectionManager.startCompression());
        registerListener(ServerDisconnectPacket.class, (ServerPacketListener<ServerDisconnectPacket>) (connection, packet) -> connectionManager.handleDisconnection(packet));
        registerListener(ServerKeepAlivePacket.class, (ServerPacketListener<ServerKeepAlivePacket>) (connection, packet) -> connectionManager.handleKeepAlive());

        registerListener(ServerSliceUploadPacket.class, (ServerPacketListener<ServerSliceUploadPacket>) (connection, packet) -> fileTransfersManager.handleSliceUpload(connection, packet.getSlice()));
        registerListener(ServerPreTransferPacket.class, (ServerPacketListener<ServerPreTransferPacket>) (connection, packet) -> fileTransfersManager.handlePreTransfer(connection, packet.getFileIdentifier(), packet.getInformation()));
        registerListener(ServerSliceReceivedPacket.class, (ServerPacketListener<ServerSliceReceivedPacket>) fileTransfersManager::handleSliceReceived);
        registerListener(FileInformationPacket.class, (ServerPacketListener<FileInformationPacket>) (connection, packet) -> filesManager.handleFileInformation(packet));
        registerListener(ServerTransferResultPacket.class, (ServerPacketListener<ServerTransferResultPacket>) (connection, packet) -> fileTransfersManager.handleTransferResult(packet));
    }

    /**
     * Register packet listener.
     *
     * @param listener {@link ServerPacketListener} to register.
     */
    public void registerListener(Class<? extends ServerPacket> clazz, ServerPacketListener<?> listener) {
        this.listenersManager.register(clazz, listener);
    }
}
