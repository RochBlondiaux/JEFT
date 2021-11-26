package me.rochblondiaux.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.commons.models.slicer.PizzaioloSlicer;
import me.rochblondiaux.server.files.FilesManager;
import me.rochblondiaux.server.network.netty.NettyServer;
import me.rochblondiaux.server.storage.StorageManager;
import me.rochblondiaux.server.terminal.DefaultTerminal;
import me.rochblondiaux.server.terminal.ITerminal;
import me.rochblondiaux.server.transfers.FileTransferManager;
import me.rochblondiaux.server.transfers.slices.ServerSliceHandler;
import me.rochblondiaux.server.transfers.slices.ServerSliceTransporter;
import me.rochblondiaux.storage.flat.sqlite.SQLiteFactory;
import me.rochblondiaux.storage.models.SQLConnectionFactory;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.sql.SQLException;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
@Slf4j(topic = "JEFT Server")
public class Server {

    /* Netty */
    private final NettyServer nettyServer;

    /* Data Folders */
    private final File dataFolder;
    private final File logsFolder;

    /* Terminal */
    private final ITerminal terminal;

    /* Transfers */
    private final FileTransferManager fileTransferManager;

    /* Storage */
    private final SQLConnectionFactory connectionFactory;
    private final StorageManager storageManager;

    /* Files */
    private final FilesManager filesManager;


    public Server(String hostname, int port) {
        /* Shutdown */
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        /* Data Folder */
        this.dataFolder = new File(new File("").getAbsolutePath(), "server");
        this.logsFolder = new File(dataFolder, "logs");
        this.initializeDataFolders();

        /* Logger */
        System.setProperty("logsFolder", logsFolder.getPath() + File.separator);
        PropertyConfigurator.configure(getClass().getResource("/log4j.properties"));

        /* Terminal */
        this.terminal = new DefaultTerminal(this);

        /* Header */
        log.info("JEFT Server");
        log.info("Developed by Roch Blondiaux.");
        log.info("www.roch-blondiaux.com");

        log.info("Starting up...");
        long startTime = System.currentTimeMillis();

        /* Terminal */
        this.terminal.start();

        /* Storage */
        this.connectionFactory = new SQLiteFactory(new File(dataFolder, "database.db").toPath());
        this.storageManager = new StorageManager(connectionFactory);

        /* Files */
        this.filesManager = new FilesManager(storageManager, dataFolder);

        /* Transfers */
        this.fileTransferManager = new FileTransferManager(filesManager, new ServerSliceTransporter(), new ServerSliceHandler(), new PizzaioloSlicer());

        /* Netty Server */
        this.nettyServer = new NettyServer(this, hostname, port);
        this.nettyServer.init();
        this.nettyServer.start();

        log.info("Server successfully started! Took {} ms.", System.currentTimeMillis() - startTime);
    }

    private void initializeDataFolders() {
        if (!dataFolder.exists()) {
            log.warn("Data folder don't exists!");
            log.info("Creating data folder...");
            if (dataFolder.mkdir())
                log.info("Data folder successfully created!");
            else
                log.error("Cannot create data folder! Check folder permissions!");
        }
        if (!logsFolder.exists()) {
            log.warn("Logs folder don't exists!");
            log.info("Creating logs folder...");
            if (logsFolder.mkdir())
                log.info("Logs folder successfully created!");
            else
                log.error("Cannot create logs folder! Check folder permissions!");
        }
    }

    public void shutdown() {
        log.info("Stopping server...");
        log.info("Stopping sql connection factory...");
        try {
            connectionFactory.shutdown();
            log.info("SQL connection factory successfully stopped!");
        } catch (SQLException e) {
            log.error("Cannot stop SQL connection factory!", e);
        }
        nettyServer.stop();
        terminal.stop();
        log.info("Server stopped successfully!");
    }
}
