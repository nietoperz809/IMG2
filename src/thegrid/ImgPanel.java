package thegrid;

import Catalano.Imaging.FastBitmap;
import common.*;
import dialogs.TextParamBox;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ImgPanel extends JPanel {

    private final TheGrid grid;
    private BufferedImage image;
    public Point2D.Float offset = new Point2D.Float();

    final ImageView theView;

    public static final int SCROLLAMOUNT = 10;

    private final UndoStack<BufferedImage> stack = new UndoStack<>(10);

    public ImgPanel (TheGrid grid, BufferedImage img, ImageView parent) {
        super();
        this.grid = grid;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    TextParamBox.xmain(ImgPanel.this, e);
                }
            }
        });
        image = img;
        theView = parent;
        setSize(img.getWidth(), img.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }

    public void undo() {
        BufferedImage img = stack.pop();
        if (img != null) {
            image = img;
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    public void setImage (FastBitmap fb) {
        BufferedImage bimg = fb.toBufferedImage();
        setImage(bimg);
    }

    private void autoSaveImage() {
        String hp = grid.getHistoryPath();
        if (hp != null) {
            SwingUtilities.invokeLater(() -> theView.saveImageAsFile (false, hp));
        }
    }

    public void setImageCentered (BufferedImage img) {
        setImage(img);
        center(new Point2D.Double(img.getWidth(), img.getHeight()));
    }


    public void setImage (BufferedImage img) {
        if (image != null)
            stack.push (ImgTools.deepCopy(image));
        image = img;
        //String hp = grid.getHistoryPath();
        autoSaveImage();
        SwingUtilities.invokeLater(this::repaint);
    }

    public static void paintText(Graphics2D g, Point pos, Font fnt, String txt, Color col, float alpha,
                                 boolean ground) {
        if (ground) {
            FontRenderContext frc = g.getFontRenderContext();
            Rectangle2D textBound = fnt.getStringBounds(txt, frc);
            //System.out.println(textBound);
            g.setPaint (Tools.getComplementaryColor(col));
            g.fillRect((int) textBound.getX() + pos.x-2, (int) textBound.getY() + pos.y-2,
                    (int) textBound.getWidth()+2, (int) textBound.getHeight());
        }
        // ------------------
        Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(c);
        g.setPaint(col);
        g.setFont(fnt);
        g.drawString(txt, pos.x, pos.y);
    }

    public void center(Point2D.Double p) {
        double w1 = getWidth()/2.0;
        double w2 = (float)p.getX()/2.0;
        double h1 = getHeight()/2.0;
        double h2 = p.getY()/2.0;
        offset.x = (float) (w1-w2);
        offset.y = (float) (h1-h2);
    }

    public void clearOffset() {
        offset = new Point2D.Float();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, (int)offset.x, (int)offset.y, this);
    }

    public Point2D.Float getOffset() {
        return offset;
    }

    public void scrollRight() {
        offset.x += SCROLLAMOUNT;
        repaint();
    }

    public void scrollLeft() {
        offset.x -= SCROLLAMOUNT;
        repaint();
    }

    public void scrollDown() {
        offset.y += SCROLLAMOUNT;
        repaint();
    }

    public void scrollUp() {
        offset.y -= SCROLLAMOUNT;
        repaint();
    }

    public void setWatermark(Watermark watermark) {
        if (image != null) {
            stack.push (ImgTools.deepCopy(image));
            paintText(image.createGraphics(), watermark.pos, watermark.font,
                    watermark.text, watermark.col, watermark.alpha, watermark.fillground);
            autoSaveImage();
            SwingUtilities.invokeLater(this::repaint);
        }
    }
}
