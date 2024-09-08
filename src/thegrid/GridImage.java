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

    private static final LinkedList<GridImage> tempImgBuffer = new LinkedList<>();

    private final byte[] imgHash;
    private DBHandler.NameID thisID;
    private JPanel rootPane;

    public byte[] getHash() {
        return imgHash;
    }
    public int getRowID() {
        return thisID.rowid();
    }

    void hide (List<String> tags) {
        if (tags.isEmpty()) {
            GridImage g;
            for(;;) {
                g = tempImgBuffer.poll();
                if (g == null)
                    break;
                rootPane.add(g);
            }
            return;
        }
        boolean hidden = true;
        for (String s : tags) {
            if (s.equals(thisID.tag())) {
                hidden = false;
                break;
            }
        }
        if (hidden) {
            tempImgBuffer.add(this);
            rootPane.remove(this);
        }
    }

    private void init (TheGrid grid, int index, JPanel jp) {
        rootPane = jp;
        thisID = grid.imageL.get(index);
        setToolTipText (thisID.name()+" -- right mouse button to delete");
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        String tag = thisID.tag() == null ? "": thisID.tag()+" : ";
        setText(tag+thisID.rowid());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) { // right click
                    if (!Tools.Question("Really delete "+thisID.rowid()+"?"))
                        return;
                    if (DBHandler.getInst().deleteImage(thisID.rowid())) {
                        rootPane.remove(GridImage.this);
                        rootPane.doLayout();
                        rootPane.repaint();
                    }
                    return;
                }
                ImageView iv = new ImageView (grid,index); // left click
                grid.controller.add (iv);
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
        imgHash = ImgTools.imgHash((BufferedImage)iconImage);
        init (grid, index, rootPane);
    }

    /**
     * Constructor for initial fill of the ImageGrid
     * @param currentIndex index of current image file
     * @param rootPane the Imagegrid itself
     */
    GridImage(TheGrid grid, DBHandler.ThumbHash tbh, int currentIndex, JPanel rootPane) {
        super(new ImageIcon(tbh.img));
        imgHash = tbh.hash;
        init (grid, currentIndex, rootPane);
    }
}
