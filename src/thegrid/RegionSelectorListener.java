package thegrid;

import common.ImageWarper;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class RegionSelectorListener extends MouseAdapter {
    final ImageView theView;
    final ImgPanel imgPanel;
    private final float A;
    private final float B;
    Rectangle box = null;
    Graphics2D g2d;
    Point pressed  = null;
    Rectangle before = null;
    final ImageView parent;
    boolean useWarper = false;

    public RegionSelectorListener(BufferedImage img, ImgPanel thePanel, ImageView p) {
        theView = p;
        A = img.getHeight();
        B = img.getWidth();
        this.imgPanel = thePanel;
        parent = p;
        thePanel.addMouseListener(this);
        thePanel.addMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        pressed = e.getPoint();
        if (e.isControlDown()) {
            useWarper = true;
            return;
        }
        useWarper = false;
        box = new Rectangle();
        box.x = e.getX();
        box.y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (pressed == null)
            return;
        if (useWarper) {
            ImageWarper warp = new ImageWarper(imgPanel.getImage(),pressed, e.getPoint());
            BufferedImage bi2 = warp.warpPixels();
            imgPanel.setImage(bi2);
            return;
        }
        //System.out.println("released "+ e.getPoint());
        calcBox(box,e);
        if (box.width > 10 && box.height > 10) {
            g2d = (Graphics2D) imgPanel.getGraphics();
            g2d.setStroke(new BasicStroke(4));
            g2d.setPaintMode();
            g2d.setColor(Color.RED);
            g2d.drawRect(box.x, box.y, box.width, box.height);

            Rectangle r2 = new Rectangle(box);
            Point p = imgPanel.getOffset();
            r2.x -= p.x;
            r2.y -= p.y;
            parent.zoomIn(r2);
        }
        pressed = null;
        before= null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (pressed == null || useWarper)
            return;
        Rectangle rect = new Rectangle(pressed);
        calcBox(rect, e);
        g2d = (Graphics2D) imgPanel.getGraphics();
        g2d.setStroke(new BasicStroke(4));
        g2d.setXORMode (Color.RED);
        if (before != null) {
            g2d.drawRect(before.x, before.y, before.width, before.height);
        }

        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
        before = new Rectangle(rect);
    }

    private void calcBox (Rectangle r, MouseEvent e) {
        r.width = Math.abs(e.getX() - r.x);
        r.height = Math.abs(e.getY() - r.y);
        r.x = Math.min(r.x, e.getX());
        r.y = Math.min(r.y, e.getY());
    }
}
