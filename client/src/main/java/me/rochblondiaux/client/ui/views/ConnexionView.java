package me.rochblondiaux.client.ui.views;

import lombok.Getter;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.ui.listeners.ConnectButtonListener;
import me.rochblondiaux.client.utils.LocalizationUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
public class ConnexionView extends AbstractView {

    /* Buttons */
    private JButton connectBtn;

    /* Fields */
    private JTextField hostnameField;

    /* Labels */
    private JLabel hostnameLabel;

    public ConnexionView(Client client, JFrame frame) throws HeadlessException {
        super(client, frame, "JEFT - Connexion");
        if (Objects.nonNull(Client.get().getNettyClient().getConnectionManager().getServer()))
            Client.get().getViewsController().setCurrentView(MainView.class);
    }

    @Override
    public void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();

        /* Hostname label */
        hostnameLabel = new JLabel();
        hostnameLabel.setText(LocalizationUtil.getLocalization("hostname"));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        add(hostnameLabel, gbc);

        /* Hostname field */
        hostnameField = new JTextField();
        hostnameField.setColumns(20);
        hostnameField.setToolTipText(LocalizationUtil.getLocalization("hostname.tooltip"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        add(hostnameField, gbc);

        /* Connect Button */
        connectBtn = new JButton();
        connectBtn.setText(LocalizationUtil.getLocalization("connect"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        add(connectBtn, gbc);

        connectBtn.addActionListener(new ConnectButtonListener(client, this));
    }
}
