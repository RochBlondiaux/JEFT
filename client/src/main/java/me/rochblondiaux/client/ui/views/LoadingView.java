package me.rochblondiaux.client.ui.views;

import lombok.Getter;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.utils.ImageUtils;
import me.rochblondiaux.client.utils.LocalizationUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
public class LoadingView extends AbstractView {

    private final String message;
    private final String icon;
    private JProgressBar progressBar;
    private JLabel label;

    public LoadingView(Client client, String title, String message) throws HeadlessException {
        super(client, client.getViewsController().getWindow(), "JEFT - " + title);
        this.message = message;
        this.icon = "/loaders/upload.gif";
    }

    public LoadingView(Client client, String title, String message, String icon) throws HeadlessException {
        super(client, client.getViewsController().getWindow(), "JEFT - " + title);
        this.message = message;
        this.icon = icon;
    }

    @Override
    public void initializeComponents() {
        /* Content panes */
        setLayout(new GridBagLayout());
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(panel2, gbc);

        /* Info Label */
        ImageIcon loading = new ImageIcon(ImageUtils.getResourcesURL(icon));
        label = new JLabel(LocalizationUtil.getLocalization(message), loading, JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        add(label);

        /* Content Pane */
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(panel3, gbc);

        /* Progress Bar */
        progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        panel2.add(progressBar);
    }
}
