package me.rochblondiaux.client.ui.views;

import me.rochblondiaux.client.Client;
import me.rochblondiaux.client.files.FilesManager;
import me.rochblondiaux.client.ui.components.ButtonColumn;
import me.rochblondiaux.client.ui.listeners.NavigationButtonListener;
import me.rochblondiaux.client.utils.LocalizationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.stream.Collectors;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class DownloadsView extends AbstractView {

    private final FilesManager manager;

    public DownloadsView(Client client, JFrame frame) throws HeadlessException {
        super(client, frame, "JEFT - Downloads");
        this.manager = client.getFilesManager();
    }

    @Override
    public void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        DefaultTableModel model = new DefaultTableModel(manager.getFiles()
                .stream()
                .sorted((o1, o2) -> o2.getUploadDate().compareTo(o1.getUploadDate()))
                .map(fileInformation -> new Object[]{fileInformation.getName(),
                        humanReadableByteCountBin(fileInformation.getSize()),
                        new SimpleDateFormat("h:mm a dd/MM/yyyy").format(fileInformation.getUploadDate()),
                        "Download"
                })
                .collect(Collectors.toList())
                .toArray(new Object[][]{}), new String[]{"Name", "Size", "Upload date", "Action"});
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.parseInt(e.getActionCommand());
                manager.downloadFile(table.getModel().getValueAt(modelRow, 0).toString());
            }
        };
        ButtonColumn buttonColumn = new ButtonColumn(table, delete, 3);
        table.getColumnModel().getColumn(3).setCellRenderer(buttonColumn);
        table.setCellSelectionEnabled(false);
        add(table, gbc);

        /* Back Button */
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        JButton backButton = new JButton();
        backButton.setText(LocalizationUtil.getLocalization("back"));
        add(backButton, gbc);
        backButton.addActionListener(new NavigationButtonListener(client, MainView.class));
    }

    private String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024)
            return bytes + " B";
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }
}
