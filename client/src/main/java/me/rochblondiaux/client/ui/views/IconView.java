package me.rochblondiaux.client.ui.views;

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
public class IconView extends AbstractView {

    private final String message;
    private final String icon;
    private final Class<? extends AbstractView> redirection;

    public IconView(Client client, String title, String message) throws HeadlessException {
        super(client, client.getViewsController().getWindow(), "JEFT - " + title);
        this.message = message;
        this.icon = "/loaders/error.gif";
        this.redirection = MainView.class;
    }

    public IconView(Client client, String title, String message, String icon) throws HeadlessException {
        super(client, client.getViewsController().getWindow(), "JEFT - " + title);
        this.message = message;
        this.icon = icon;
        this.redirection = MainView.class;
    }

    public IconView(Client client, String title, String message, String icon, Class<? extends AbstractView> redirection) throws HeadlessException {
        super(client, client.getViewsController().getWindow(), "JEFT - " + title);
        this.message = message;
        this.icon = icon;
        this.redirection = redirection;
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

        /* Back Btn */
        JButton backBtn = new JButton();
        backBtn.setText(LocalizationUtil.getLocalization("back"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel2.add(backBtn, gbc);
        backBtn.addActionListener(new NavigationButtonListener(client, redirection));

        /* Content Pane */
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(panel3, gbc);

        /* Information Label */
        ImageIcon loading = new ImageIcon(ImageUtils.getResourcesURL(icon));
        final JLabel informationLabel = new JLabel(LocalizationUtil.getLocalization(message), loading, JLabel.CENTER);
        informationLabel.setVerticalTextPosition(JLabel.BOTTOM);
        informationLabel.setHorizontalTextPosition(JLabel.CENTER);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel3.add(informationLabel, gbc);
    }
}
