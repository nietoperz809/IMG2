package thegrid;

import common.ImgTools;
import common.Tools;
import database.DBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

public class GridImage extends JLabel {
    final static Color markedColor = Color.RED;
    final static Color unmarkedColor = null;
    private DBHandler.NameID thisID;
    private JPanel rootPane;

    static public void markAll (TheGrid grid, boolean mark) {
        Component[] comp = grid.rootPane.getComponents();
        for (Component c : comp) {
            GridImage img = (GridImage)c;
            img.setMarked(mark);
            img.repaint();
        }
    }

    static public void toggleMarks (TheGrid grid) {
        Component[] comp = grid.rootPane.getComponents();
        for (Component c : comp) {
            GridImage img = (GridImage)c;
            img.setMarked(!img.isMarked());
            img.repaint();
        }
    }

    static public GridImage[] getMarked(TheGrid grid) {
        ArrayList<GridImage> marked = new ArrayList<>();
        Component[] comp = grid.rootPane.getComponents();
        for (Component c : comp) {
            GridImage img = (GridImage)c;
            if (img.isMarked())
                marked.add(img);
        }
        return marked.toArray(new GridImage[0]);
    }


//    public byte[] getHash() {
//        return imgHash;
//    }

    public int getRowID() {
        return thisID.rowid();
    }

    public boolean isMarked() {
        return getBackground() == markedColor;
    }

    public void setMarked(boolean mark) {
        setOpaque(true);
        if (mark)
            setBackground (markedColor);
        else
            setBackground (unmarkedColor);
    }

    private void init (TheGrid grid, int index, JPanel jp) {
        rootPane = jp;
        thisID = grid.imageL.get(index);
        setToolTipText (thisID.name()+
                " right mouse button to delete\n shift&rmb to renew thumb");
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        setText (String.valueOf(thisID.rowid()));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // right click
                    if (e.isShiftDown()) {
                        DBHandler.createNewThumb(thisID.rowid());
                        Tools.Info("New thumbnail created for: "+thisID.rowid());
                        return;
                    }
                    if (Tools.Question("Really delete "+thisID.rowid()+"?")) {
                        if (DBHandler.deleteImage(thisID.rowid())) {
                            rootPane.remove(GridImage.this);
                            rootPane.doLayout();
                            rootPane.repaint();
                        }
                    }
                }
                // left click
                else if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.isControlDown()) {
                        setOpaque(true);
                        if (getBackground() == markedColor) {
                            setBackground(unmarkedColor);
                        } else {
                            GridImage.this.setMarked(true);
                            //setBackground(markedColor);
                            //marked.add(GridImage.this);
                        }
                        return;
                    }
                    ImageView iv = new ImageView(grid, index);
                    markAll(grid, false);
                    grid.controller.add(iv);
                }
            }
        });
    }

    /**
     * Constructor for later insertion of new Images
     * @param iconImage smaller Icon image
     * @param rootPane the Imagegrid itself
     * @param ImageName name of the new Image
     */
    GridImage(TheGrid grid, Image iconImage, JPanel rootPane, String ImageName) throws Exception {
        super(new ImageIcon(iconImage));
        grid.imageL.addNameID(new DBHandler.NameID(ImageName, grid.imageL.getLastRowid(), null));
        int index = grid.imageL.size()-1;
        //imgHash = ImgTools.imgHash((BufferedImage)iconImage);
        init (grid, index, rootPane);
    }

    /**
     * Constructor for initial fill of the ImageGrid
     * @param currentIndex index of current image file
     * @param rootPane the Imagegrid itself
     */
    GridImage(TheGrid grid, byte[] tbh, int currentIndex, JPanel rootPane) {
        super(new ImageIcon(ImgTools.byteArrayToImg(tbh)));
        //imgHash = null;
        init (grid, currentIndex, rootPane);
    }
}
