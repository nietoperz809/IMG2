package thegrid;

import imageloader.ImageStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

class GridImage extends JLabel {
    GridImage(Image image, List<String> files, int index, ImageStore ims, JPanel rootPane) {
        super(new ImageIcon(image));
        String thisName = files.get(index);
        setToolTipText (thisName+" -- right mouse button to delete");
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    if (ims.delete(thisName)) {
                        rootPane.remove(GridImage.this);
                        rootPane.doLayout();
                    }
                    return;
                }
                new ImageView(files, index, ims);
            }
        });

    }
}
