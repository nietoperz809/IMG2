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

    private static LinkedList<GridImage> tempImgBuffer = new LinkedList<>();

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
            if (s.equals(thisID.tag()))
                hidden = false;
        }
        if (hidden) {
            tempImgBuffer.add(this);
            rootPane.remove(this);
        }
    }

    private void init (List<DBHandler.NameID> files, int index, JPanel jp) {
        rootPane = jp;
        thisID = files.get(index);
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
                new ImageView(files, index); // left click
            }
        });
    }

    /**
     * Constructor for later insertion of new Images
     * @param iconImage smaller Icon image
     * @param files list of all names of images in the Image store
     * @param rootPane the Imagegrid itself
     * @param ImageName name of the new Image
     */
    GridImage(Image iconImage, List<DBHandler.NameID> files,
              JPanel rootPane, String ImageName) throws Exception {
        super(new ImageIcon(iconImage));
        files.add (new DBHandler.NameID(ImageName, -1, null)); //(ImageName);
        int index = files.size()-1;
        imgHash = ImgTools.imgHash((BufferedImage)iconImage);
        init (files, index, rootPane);
    }

    /**
     * Constructor for initial fill of the ImageGrid
     * @param files list of all names of images in the Image store
     * @param currentIndex index of current image file
     * @param rootPane the Imagegrid itself
     */
    GridImage(DBHandler.ThumbHash tbh, List<DBHandler.NameID> files, int currentIndex, JPanel rootPane) {
        super(new ImageIcon(tbh.img));
        imgHash = tbh.hash;
        init (files, currentIndex, rootPane);
    }
}
