package me.rochblondiaux.client.ui.listeners;

import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.network.ConnectionResult;
import me.rochblondiaux.client.ui.controllers.NetworkController;
import me.rochblondiaux.client.ui.views.ConnectingView;
import me.rochblondiaux.client.ui.views.ConnexionView;
import me.rochblondiaux.client.ui.views.IconView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ConnectButtonListener implements ActionListener {

    private final Client client;
    private final NetworkController controller;
    private final ConnexionView view;

    public ConnectButtonListener(Client client, ConnexionView view) {
        this.client = client;
        this.controller = client.getNetworkController();
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String rawHostName = view.getHostnameField().getText();
        if (rawHostName == null || rawHostName.isEmpty()) return;
        client.getViewsController().setCurrentView(ConnectingView.class);
        controller.connect(rawHostName).thenAccept(result -> {
            if (result != ConnectionResult.SUCCESSFUL)
                client.getViewsController().setCurrentView(new IconView(client, result.getLocalizable(), "connection.refused", "/loaders/error.gif", ConnexionView.class));
        });
    }
}
