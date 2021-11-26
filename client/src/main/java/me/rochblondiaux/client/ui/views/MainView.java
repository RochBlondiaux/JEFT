package me.rochblondiaux.client.ui.views;

import lombok.Getter;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.ui.listeners.NavigationButtonListener;
import me.rochblondiaux.client.utils.ImageUtils;
import me.rochblondiaux.client.utils.LocalizationUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
public class MainView extends AbstractView {

    /* Buttons */
    private JButton uploadBtn;
    private JButton downloadBtn;

    /* Labels */
    private JLabel logoLabel;
    private JLabel creditsLabel;

    public MainView(Client client, JFrame frame) throws HeadlessException {
        super(client, frame, "JEFT - Menu");
    }

    @Override
    public void initializeComponents() {
        /* Upload Button */
        uploadBtn = new JButton();
        uploadBtn.setHorizontalAlignment(0);
        uploadBtn.setHorizontalTextPosition(11);
        uploadBtn.setText(LocalizationUtil.getLocalization("upload"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        uploadBtn.setIcon(ImageUtils.resizeImageIcon(ImageUtils.getIconFromResources("upload.png"), 25, 25));
        add(uploadBtn, gbc);
        uploadBtn.addActionListener(new NavigationButtonListener(client, UploadView.class));

        /* Download Button */
        downloadBtn = new JButton();
        downloadBtn.setText(LocalizationUtil.getLocalization("downloads"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        downloadBtn.setIcon(ImageUtils.resizeImageIcon(ImageUtils.getIconFromResources("download.png"), 25, 25));
        add(downloadBtn, gbc);
        downloadBtn.addActionListener(new NavigationButtonListener(client, DownloadsView.class));

        /* Logo */
        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(0);
        logoLabel.setHorizontalTextPosition(0);
        logoLabel.setIcon(ImageUtils.getIconFromResources("icon.png"));
        logoLabel.setText(LocalizationUtil.getLocalization("name"));
        logoLabel.setVerticalAlignment(3);
        logoLabel.setVerticalTextPosition(3);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(logoLabel, gbc);

        /* Credits */
        creditsLabel = new JLabel();
        creditsLabel.setText(LocalizationUtil.getLocalization("credits"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        add(creditsLabel, gbc);
    }
}
