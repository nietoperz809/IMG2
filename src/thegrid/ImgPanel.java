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
        repaint();
    }

    public void clearOffset() {
        offset = new Point();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, offset.x, offset.y, this);
    }

    public Point getOffset() {
        return offset;
    }

    public void scrollRight() {
        offset.x += 10;
        repaint();
    }

    public void scrollLeft() {
        offset.x -= 10;
        repaint();
    }

    public void scrollDown() {
        offset.y += 10;
        repaint();
    }

    public void scrollUp() {
        offset.y -= 10;
        repaint();
    }
}
