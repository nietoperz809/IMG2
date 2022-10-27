package thegrid;

import imageloader.ImageStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

class GridImage extends JLabel {
    GridImage(Image image, java.util.List<String> files, int index, ImageStore ims) {
        super(new ImageIcon(image));
        setToolTipText (files.get(index));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ImageView(files, index, ims);
            }
        });

    }
}
