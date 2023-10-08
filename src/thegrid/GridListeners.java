package thegrid;

import common.Tools;
import database.DBHandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;

public class GridListeners implements KeyListener {
    private final TheGrid theGrid;

    public GridListeners (TheGrid g) {
        theGrid = g;
        enableDrop();
        g.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DBHandler.getInst().log("--- TheGrid ended");
                super.windowClosing(e);
                //DBHandler.getInst().close();
                System.exit(1);
            }
        });
        g.addKeyListener(this);

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

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int kc = e.getKeyCode();
        switch (kc) {
            default:
                Tools.fastScroll(kc,theGrid.scrollPane.getViewport(), true);
            break;

            case KeyEvent.VK_N:
                ImageView iv = new ImageView(0);
                iv.selectAnotherImage();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
