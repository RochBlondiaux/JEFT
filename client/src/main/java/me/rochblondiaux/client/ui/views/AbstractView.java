package me.rochblondiaux.client.ui.views;

import me.rochblondiaux.client.Client;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Abstract implementation of the View interface.
 */
public abstract class AbstractView extends JPanel implements View {

    protected final Client client;
    protected final JFrame window;

    public AbstractView(Client client, JFrame window, String title) throws HeadlessException {
        this.client = client;
        this.window = window;
        this.window.setTitle(title);
    }

    @Override
    public void initializeFrame(JFrame frame) {
        frame.setContentPane(this);
    }


    @Override
    public void initializeContentPane() {
        setLayout(new GridBagLayout());
    }
}
