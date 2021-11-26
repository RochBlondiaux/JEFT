package me.rochblondiaux.client.ui.views;

import lombok.Getter;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.ui.listeners.DragAndDropListener;
import me.rochblondiaux.client.ui.listeners.FileSelectButtonListener;
import me.rochblondiaux.client.ui.listeners.NavigationButtonListener;
import me.rochblondiaux.client.utils.ImageUtils;
import me.rochblondiaux.client.utils.LocalizationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
public class UploadView extends AbstractView {

    /* Labels */
    private JLabel uploadLabel;

    /* Buttons */
    private JButton backButton;
    private JButton selectFileButton;

    public UploadView(Client client, JFrame frame) throws HeadlessException {
        super(client, frame, "JEFT - Upload");
    }

    @Override
    public void initializeComponents() {
        setLayout(new GridBagLayout());

        /* Back */
        backButton = new JButton();
        backButton.setText(LocalizationUtil.getLocalization("back"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        add(backButton, gbc);
        backButton.addActionListener(new NavigationButtonListener(client, MainView.class));

        /* Select File */
        selectFileButton = new JButton();
        selectFileButton.setText(LocalizationUtil.getLocalization("select.file"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        add(selectFileButton, gbc);
        selectFileButton.addActionListener(new FileSelectButtonListener(this));

        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(panel2, gbc);

        /* Image */
        uploadLabel = new JLabel();
        uploadLabel.setIcon(ImageUtils.getIconFromResources("upload.png"));
        uploadLabel.setIconTextGap(15);
        uploadLabel.setOpaque(false);
        uploadLabel.setText(LocalizationUtil.getLocalization("drag.n.drop"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel2.add(uploadLabel, gbc);

        new DropTarget(uploadLabel, new DragAndDropListener());
    }
}
