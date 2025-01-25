package thegrid;

import common.ImgTools;
import common.Tools;
import database.DBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

class GridImage extends JLabel {

    private static final LinkedList<GridImage> marked = new LinkedList<>();
    final static Color markedColor = Color.RED;
    final static Color unmarkedColor = null;
    //private final byte[] imgHash;
    private DBHandler.NameID thisID;
    private JPanel rootPane;

    static public void unmarkAll() {
        for (int i = 0; i < marked.size(); i++) {
            GridImage img = marked.get(i);
            img.setBackground(unmarkedColor);
            //img.repaint();
        }
        marked.clear();
    }

    static public GridImage[] getMarked() {
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
                            marked.remove(GridImage.this);
                        } else {
                            setBackground(markedColor);
                            marked.add(GridImage.this);
                        }
                        return;
                    }
                    ImageView iv = new ImageView(grid, index);
                    unmarkAll();
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
        grid.imageL.add (new DBHandler.NameID(ImageName, grid.imageL.getLastRowid(), null)); //(ImageName);
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
