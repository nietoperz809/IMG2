package thegrid;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ImgPanel extends JPanel {

    private BufferedImage image;
    private Point offset = new Point();

    public ImgPanel (BufferedImage img) {
        super();
        image = img;
        setSize(img.getWidth(), img.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage (BufferedImage img) {
        image = img;
        offset = new Point();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, offset.x, offset.y, this);
    }

    public Point getOffset() {
        return offset;
    }

    public void right() {
        int a = getWidth()-image.getWidth()+10;
        int x = offset.x + 10;
        if (x <= a)
            offset.x = x;
        repaint();
    }
    public void left() {
        int x = offset.x - 10;
        if (x >= 0)
            offset.x = x;
        repaint();
    }

    public void down() {
        int a = getHeight()-image.getHeight()+10;
        int y = offset.y + 10;
        if (y <= a)
            offset.y = y;
        repaint();
    }

    public void up() {
        int y = offset.y - 10;
        if (y >= 0)
            offset.y = y;
        repaint();
    }
}
