package me.rochblondiaux.client.ui.views;

import javax.swing.*;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Views interfaced model.
 */
public interface View {

    /**
     * Method responsible for setting up view's window.
     */
    void initializeFrame(JFrame frame);

    /**
     * Method responsible for setting up view's content pane.
     * Called after {@link View#initializeFrame(JFrame)}
     */
    void initializeContentPane();

    /**
     * Method responsible for setting up all view's components.
     * Called after {@link View#initializeContentPane()}
     */
    void initializeComponents();
}
