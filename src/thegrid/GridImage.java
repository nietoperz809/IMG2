package thegrid;

import imageloader.ImageStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

class GridImage extends JLabel {

    volatile String thisName;

    public void setName(String n) {
        thisName = n;
    }

    GridImage(Image image, List<String> files, int index, ImageStore ims, JPanel rootPane) {
        super(new ImageIcon(image));
        if (-1 == index) {
            thisName = "NEW-IMAGE";
        } else {
            thisName = files.get(index);
        }
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
}
