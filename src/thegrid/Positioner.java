package thegrid;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public interface Positioner {
    int SCROLLAMOUNT = 10;
    Point2D.Float offset = new Point2D.Float();

    default void center (JPanel jp, Point2D.Double p) {
        double w1 = jp.getWidth()/2.0;
        double w2 = (float)p.getX()/2.0;
        double h1 = jp.getHeight()/2.0;
        double h2 = p.getY()/2.0;
        offset.x = (float) (w1-w2);
        offset.y = (float) (h1-h2);
    }

    default void clearOffset() {
        offset.x = 0;
        offset.y = 0;
    }

    default Point2D.Float getOffset() {
        return offset;
    }

    default void scrollRight(Component c) {
        offset.x += SCROLLAMOUNT;
        c.repaint();
    }

    default void scrollLeft(Component p) {
        offset.x -= SCROLLAMOUNT;
        p.repaint();
    }


    default void scrollDown(Component p) {
        offset.y += SCROLLAMOUNT;
        p.repaint();
    }

    default void scrollUp(Component p) {
        offset.y -= SCROLLAMOUNT;
        p.repaint();
    }

}
