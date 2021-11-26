package me.rochblondiaux.client.ui.listeners;

import lombok.RequiredArgsConstructor;
import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.ui.views.AbstractView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Class reponsive for navigation button click.
 */
@RequiredArgsConstructor
public class NavigationButtonListener implements ActionListener {

    private final Client client;
    private final Class<? extends AbstractView> nextView;


    @Override
    public void actionPerformed(ActionEvent e) {
        client.getViewsController().setCurrentView(nextView);
    }

}
