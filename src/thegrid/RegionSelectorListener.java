package thegrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegionSelectorListener extends MouseAdapter {
    final JLabel label;
    Rectangle box = null;
    Graphics2D g2d;
    Point pressed  = null;
    Rectangle before = null;
    final ImageView parent;

    public RegionSelectorListener(JLabel theLabel, ImageView p) {
        this.label = theLabel;
        parent = p;
        theLabel.addMouseListener(this);
        theLabel.addMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        //System.out.println("pressed "+ e.getPoint());
        pressed = e.getPoint();
        box = new Rectangle();
        box.x = e.getX();
        box.y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (pressed == null)
            return;
        //System.out.println("released "+ e.getPoint());
        calcBox(box,e);
        g2d = (Graphics2D)label.getGraphics();
        g2d.setStroke(new BasicStroke(4));
        g2d.setPaintMode();
        g2d.setColor (Color.RED);
        g2d.drawRect(box.x, box.y, box.width, box.height);
        parent.zoomIn(box);
        pressed = null;
        before= null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (pressed == null)
            return;
        //System.out.println("dragged "+ e.getPoint());
        Rectangle rect = new Rectangle(pressed);
        calcBox(rect, e);
        g2d = (Graphics2D)label.getGraphics();
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
