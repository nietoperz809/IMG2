package thegrid;

import imageloader.DBHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.Preferences;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;

public class GridListeners {
    private final TheGrid theGrid;

    public GridListeners (TheGrid g) {
        theGrid = g;
        enableDrop();
        g.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                DBHandler.getInst().close();
                System.exit(1);
            }
        });

    }

    private void enableDrop() {
        new DropTarget(theGrid, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = event.getTransferable();
                DataFlavor[] flavors = transferable.getTransferDataFlavors();
                for (DataFlavor flavor : flavors) {
                    if (flavor.isFlavorJavaFileListType()) {
                        java.util.List<File> files;
                        try {
                            files = (java.util.List<File>) transferable.getTransferData(flavor);
                            File[] array = files.toArray(new File[0]);
                            theGrid.addImageFilesToDatabase(array);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return; // only one file
                    }
                }
            }
        });
    }
}
