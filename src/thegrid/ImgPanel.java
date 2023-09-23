package thegrid;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ImgPanel extends JPanel {

    private BufferedImage image;
    private Point offset = new Point();

    JToolTip thisJT;

    public ImgPanel (BufferedImage img) {
        super();
        image = img;
        setSize(img.getWidth(), img.getHeight());
    }

    /**
     * set TT Location
     * @param event  the <code>MouseEvent</code> that caused the
     *          <code>ToolTipManager</code> to show the tooltip
     * @return Point to set the TT
     */
    public Point getToolTipLocation(MouseEvent event) {
        if (thisJT == null)
            return null;
        return new Point(0, this.getHeight()-thisJT.getHeight());
    }

    public JToolTip createToolTip() {
        thisJT = super.createToolTip();
        return thisJT;
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
