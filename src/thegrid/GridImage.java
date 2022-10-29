package thegrid;

import imageloader.ImageStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

class GridImage extends JLabel {

    private void init (List<String> files, int index, ImageStore ims, JPanel rootPane) {
        String thisName = files.get(index);
        setToolTipText (thisName+" -- right mouse button to delete");
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    if (ims.delete(thisName)) {
                        rootPane.remove(GridImage.this);
                        rootPane.doLayout();
                        rootPane.repaint();
                    }
                    return;
                }
                new ImageView(files, index, ims);
            }
        });
    }

    /**
     * Constructer for later insertion of new Images
     * @param iconImage smaller Icon image
     * @param files list of all names of images in the Image store
     * @param ims the Image store
     * @param rootPane the Imagegrid itself
     * @param ImageName name of the new Image
     */
    GridImage(Image iconImage, List<String> files,
              ImageStore ims, JPanel rootPane, String ImageName) {
        super(new ImageIcon(iconImage));
        files.add(ImageName);
        int index = files.size()-1;
        init (files, index, ims, rootPane);
    }

    /**
     * Constructor for intial fill of the ImageGrid
     * @param IconImage smaller Icon image
     * @param files list of all names of images in the Image store
     * @param currentIndex indes of current image file
     * @param ims the Image store
     * @param rootPane the Imagegrid itself
     */
    GridImage(Image IconImage, List<String> files, int currentIndex, ImageStore ims, JPanel rootPane) {
        super(new ImageIcon(IconImage));
        init (files, currentIndex, ims, rootPane);
    }
}
