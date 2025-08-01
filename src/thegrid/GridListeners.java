package thegrid;

import common.Tools;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import static common.SystemClipboard.completeImagelinks;

public class GridListeners implements KeyListener {
    private final TheGrid theGrid;

    private void dispose(boolean shutdown_allowed) {
        if (theGrid.thisInstCount > 1) { // not the last grid?
            theGrid.dispose();
            return;
        }
        if (shutdown_allowed) {
            Tools.shutdown(theGrid);
        }
    }

    public GridListeners(TheGrid g) {
        theGrid = g;
        enableDrop();
        g.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dispose(true);
            }
        });
        g.addKeyListener(this);
    }

    @SuppressWarnings("unchecked")
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
        if (kc == KeyEvent.VK_ESCAPE) {
            dispose(false);
        }
        // ctrl-v, make img tags
        else if (kc == KeyEvent.VK_V && e.isControlDown()) {
            completeImagelinks("img");
        }
        // n
        else if (kc == KeyEvent.VK_N) {
            TheGrid tg = TheGrid.getMainGrid();
            ImageView iv = new ImageView(tg, 0);
            //ImageViewController.add(iv);
            iv.selectAnotherImage(-1);
        } else {
            Tools.fastScroll(kc, theGrid.scrollPane.getViewport(), true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
