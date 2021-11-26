package me.rochblondiaux.client.ui.controllers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.ui.views.AbstractView;
import me.rochblondiaux.client.ui.views.ConnectingView;
import me.rochblondiaux.client.ui.views.ConnexionView;
import me.rochblondiaux.client.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
@Slf4j(topic = "Views Controller")
public class ViewsController {

    private final Client client;
    private final JFrame window;
    private AbstractView currentView;

    public ViewsController(Client client) {
        this.window = new JFrame();
        this.client = client;

        initFrame();

        setCurrentView(ConnexionView.class);
    }

    /**
     * Change current view with another one.
     * Actually it just replaces window content pane.
     *
     * @param view next view.
     */
    public void setCurrentView(AbstractView view) {
        view.initializeFrame(window);
        view.initializeContentPane();
        view.initializeComponents();
        this.currentView = view;
        window.setContentPane(view);
        window.pack();
    }

    /**
     * Change current view with another one by its class.
     * {@link ViewsController#setCurrentView(AbstractView)}
     *
     * @param clazz view's class
     */
    public void setCurrentView(Class<? extends AbstractView> clazz) {
        try {
            setCurrentView(clazz.getConstructor(Client.class, JFrame.class).newInstance(client, window));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.warn("Cannot instantiate view " + clazz.getName() + "!", e);
        }
    }

    /**
     * Initialize application window.
     */
    private void initFrame() {
        window.setPreferredSize(new Dimension(750, 380));
        window.setIconImage(ImageUtils.getBufferedImageFromResource("/icons/icon.png"));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setCurrentView(new ConnectingView(client, window));
        window.setVisible(true);
        window.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width / 2 - window.getSize().width / 2, dim.height / 2 - window.getSize().height / 2);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
