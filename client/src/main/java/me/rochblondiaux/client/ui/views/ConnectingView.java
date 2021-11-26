package me.rochblondiaux.client.ui.views;

import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.ui.controllers.NetworkController;
import me.rochblondiaux.client.utils.ImageUtils;
import me.rochblondiaux.client.utils.LocalizationUtil;
import me.rochblondiaux.commons.utils.StandaloneRunnable;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ConnectingView extends AbstractView {

    private final NetworkController controller;

    public ConnectingView(Client client, JFrame window) throws HeadlessException {
        super(client, window, "JEFT - Connexion");
        this.controller = client.getNetworkController();
    }

    @Override
    public void initializeComponents() {
        ImageIcon loading = new ImageIcon(ImageUtils.getResourcesURL("/loaders/rocket.gif"));
        JLabel label = new JLabel(LocalizationUtil.getLocalization("connecting"), loading, JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        add(label);

        new StandaloneRunnable() {
            @Override
            public void run() {
                if (!controller.isLoggedIn()) return;
                client.getViewsController().setCurrentView(MainView.class);
                Client.get().getFilesManager().requestFiles();
                cancel();
            }
        }.runAsync(0, 1, TimeUnit.SECONDS);
    }
}
