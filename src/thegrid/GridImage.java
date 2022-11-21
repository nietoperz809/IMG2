package thegrid;

import imageloader.DBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

class GridImage extends JLabel {

    private void init (List<DBHandler.NameID> files, int index, JPanel rootPane) {
        DBHandler.NameID thisID = files.get(index);
        setToolTipText (thisID.name+" -- right mouse button to delete");
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        setText(""+thisID.rowid);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) { // right click
                    if (!Tools.Question("Really delete "+thisID.rowid+"?"))
                        return;
                    if (DBHandler.getInst().delete(thisID.rowid)) {
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
              JPanel rootPane, String ImageName) {
        super(new ImageIcon(iconImage));
        files.add (new DBHandler.NameID(ImageName, -1)); //(ImageName);
        int index = files.size()-1;
        init (files, index, rootPane);
    }

    /**
     * Constructor for initial fill of the ImageGrid
     * @param IconImage smaller Icon image
     * @param files list of all names of images in the Image store
     * @param currentIndex index of current image file
     * @param rootPane the Imagegrid itself
     */
    GridImage(Image IconImage, List<DBHandler.NameID> files, int currentIndex, JPanel rootPane) {
        super(new ImageIcon(IconImage));
        init (files, currentIndex, rootPane);
    }
}
