package me.rochblondiaux.client;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.files.FilesManager;
import me.rochblondiaux.client.network.netty.NettyClient;
import me.rochblondiaux.client.transfers.FileTransfersManager;
import me.rochblondiaux.client.transfers.handlers.ClientTransferResultHandler;
import me.rochblondiaux.client.transfers.handlers.ViewTransferResultHandler;
import me.rochblondiaux.client.transfers.slices.ClientSliceTransporter;
import me.rochblondiaux.client.transfers.slices.handlers.ClientSliceHandler;
import me.rochblondiaux.client.transfers.slices.handlers.ViewsSliceHandler;
import me.rochblondiaux.client.ui.controllers.FilesController;
import me.rochblondiaux.client.ui.controllers.NetworkController;
import me.rochblondiaux.client.ui.controllers.ViewsController;
import me.rochblondiaux.commons.models.slicer.PizzaioloSlicer;
import me.rochblondiaux.encryption.DefaultSecurityManager;
import me.rochblondiaux.encryption.SecurityManager;
import net.harawata.appdirs.AppDirsFactory;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
@Slf4j(topic = "JEFT Client")
public class Client {

    /* Instance */
    private static Client instance;

    /* Data Folders */
    private final File dataFolder;
    private final File logsFolder;

    /* Transfers */
    private final FileTransfersManager fileTransfersManager;

    /* Files */
    private final FilesManager filesManager;

    /* Controllers */
    private final ViewsController viewsController;
    private final NetworkController networkController;
    private final FilesController filesController;

    /* Netty */
    private final NettyClient nettyClient;

    /* Encryption */
    private final SecurityManager securityManager;

    public Client() {
        /* Instance */
        instance = this;

        /* Data Folders */
        this.dataFolder = new File(AppDirsFactory.getInstance().getUserDataDir("JEFTClient", "1.0", "Roch Blondiaux", false));
        this.logsFolder = new File(AppDirsFactory.getInstance().getUserLogDir("JEFTClient", "1.0", "Roch Blondiaux"));
        this.initializeDataFolders();

        /* Logger */
        System.setProperty("logsFolder", logsFolder.getPath() + File.separator);
        PropertyConfigurator.configure(getClass().getResource("/log4j.properties"));

        /* Look And Feel */
        FlatDarculaLaf.setup();

        /* Encryption */
        this.securityManager = new DefaultSecurityManager(dataFolder);

        /* File Transfers */
        this.fileTransfersManager = new FileTransfersManager(new ClientSliceTransporter(), new PizzaioloSlicer());
        this.fileTransfersManager.registerSliceHandler(new ClientSliceHandler());

        /* Files */
        this.filesManager = new FilesManager(securityManager, getFileTransfersManager());

        /* Network */
        this.nettyClient = new NettyClient(this);
        this.nettyClient.init();

        /* Controllers */
        this.filesController = new FilesController(this);
        this.networkController = new NetworkController(nettyClient);
        this.viewsController = new ViewsController(this);
        this.filesController.setViewsController(viewsController);

        /* Files / Files Transfers */
        this.fileTransfersManager.registerSliceHandler(new ViewsSliceHandler(viewsController));
        this.fileTransfersManager.registerTransferResultHandler(new ViewTransferResultHandler(this));
        this.fileTransfersManager.registerTransferResultHandler(new ClientTransferResultHandler(this));

        /* Shutdown */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping client...");
            nettyClient.stop();
            log.info("Client successfully stopped!");
        }));
    }

    /**
     * Get current client instance.
     *
     * @return client instance
     */
    public static Client get() {
        return instance;
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
}
