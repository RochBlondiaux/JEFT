package me.rochblondiaux.client.ui.listeners;

import me.rochblondiaux.client.Client;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class DragAndDropListener implements DropTargetListener {

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent event) {
        event.acceptDrop(DnDConstants.ACTION_COPY);
        Transferable transferable = event.getTransferable();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for (DataFlavor flavor : flavors) {
            try {
                if (flavor.isFlavorJavaFileListType()) {
                    List<File> files = (List<File>) transferable.getTransferData(flavor);
                    if (files.size() > 0) Client.get().getFilesManager().uploadFile(files.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        event.dropComplete(true);
    }
}
