package me.rochblondiaux.client.ui.listeners;

import lombok.RequiredArgsConstructor;
import me.rochblondiaux.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Class reponsive of opening a file chooser on button click.
 */
@RequiredArgsConstructor
public class FileSelectButtonListener implements ActionListener {

    private final Component parent;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION)
            Client.get().getFilesManager().uploadFile(fileChooser.getSelectedFile());
    }

}
