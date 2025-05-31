package thegrid;

import common.ImageTools;
import common.MsgBox;
import database.DBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Thumbnail extends JLabel {
    final static Color markedColor = Color.RED;
    final static Color unmarkedColor = null;
    private DBHandler.NameID thisID;
    private JPanel rootPane;

    @Override
    public JToolTip createToolTip() {
        return common.Tools.createCustomToolTip (this);
    }

    static public void markAll (TheGrid grid, boolean mark) {
        Component[] comp = grid.rootPane.getComponents();
        for (Component c : comp) {
            Thumbnail img = (Thumbnail)c;
            img.setMarked(mark);
            img.repaint();
        }
    }

    static public void toggleMarks (TheGrid grid) {
        Component[] comp = grid.rootPane.getComponents();
        for (Component c : comp) {
            Thumbnail img = (Thumbnail)c;
            img.setMarked(!img.isMarked());
            img.repaint();
        }
    }

    static public Thumbnail[] getMarked(TheGrid grid) {
        ArrayList<Thumbnail> marked = new ArrayList<>();
        Component[] comp = grid.rootPane.getComponents();
        for (Component c : comp) {
            Thumbnail img = (Thumbnail)c;
            if (img.isMarked())
                marked.add(img);
        }
        return marked.toArray(new Thumbnail[0]);
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

    private void setToolTip() {
        String len = DBHandler.queryImageLen(thisID.rowid());
        int ilen = Integer.parseInt(len);
        String f = String.format("%,d Bytes", ilen);
        setToolTipText (f);
    }

    private void init (TheGrid grid, int index, JPanel jp) {
        rootPane = jp;
        thisID = grid.imageL.get(index);
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        setText (String.valueOf(thisID.rowid()));

        setToolTip();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // right click
                    if (e.isShiftDown()) {
                        DBHandler.createNewThumb(thisID.rowid());
                        MsgBox.Info("New thumbnail created for: "+thisID.rowid());
                        return;
                    }
                    if (MsgBox.Question("Really delete "+thisID.rowid()+"?")) {
                        if (DBHandler.deleteImage(thisID.rowid())) {
                            rootPane.remove(Thumbnail.this);
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
                            Thumbnail.this.setMarked(true);
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
    Thumbnail(TheGrid grid, Image iconImage, JPanel rootPane, String ImageName) {
        super(new ImageIcon(iconImage));
        grid.imageL.addNameID(new DBHandler.NameID(ImageName, grid.imageL.getLastRowid(), null));
        int index = grid.imageL.size()-1;
        init (grid, index, rootPane);
    }

    /**
     * Constructor for initial fill of the ImageGrid
     * @param currentIndex index of current image file
     * @param rootPane the Imagegrid itself
     */
    Thumbnail(TheGrid grid, byte[] tbh, int currentIndex, JPanel rootPane) {
        super(new ImageIcon(ImageTools.byteArrayToImg(tbh)));
        //imgHash = null;
        init (grid, currentIndex, rootPane);
    }
}
