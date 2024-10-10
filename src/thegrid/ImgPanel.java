package thegrid;

import Catalano.Imaging.FastBitmap;
import common.*;
import dialogs.TextParamBox;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ImgPanel extends JPanel {

    private final TheGrid grid;
    private BufferedImage image;
    public Point offset = new Point();

    JToolTip thisJT;
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

        setToolTipText
                ("<html>+/- - scale<br>" +
                        "1,2 - gamma<br>" +
                        "3,4 - change contrast<br>"+
                        "5,6,7 - denoise, dilate, oilpaint<br>"+
                        "8,9.0 - erode, bloom, histogramEQ<br>" +
                        "y, ctrl-y - heatmap<br>" +
                        "right mouse - set watermark<br>"+
                        "a - tagger<br>" +
                        "ctrl+c - copy to clipboard<br>" +
                        "ctrl+z - undo<br>" +
                        "r - rotate<br>" +
                        "c - change img in database<br>" +
                        "n - go to specific rowid<br>" +
                        "page up/down - load next/prev image<br>" +
                        "up/down/left/right - move image<br>" +
                        "w,h - scale to width or height<br>" +
                        "l - reload<br>" +
                        "esc - close window<br>" +
                        "s - slideshow<br>" +
                        "f - save original img to file<br>"+
                        "g - save manipulated img to file<br>"+
                        "x - sharpen<br>"+
                        "t - random image forward<br>" +
                        "z - random image backwardt<br>" +
                        "d - delete from database<br>" +
                        "m - mirror</html>");
    }

    /**
     * set TT Location
     * @param event  the <code>MouseEvent</code> that caused the
     *          <code>ToolTipManager</code> to show the tooltip
     * @return Point to set the TT
     */
    @Override
    public Point getToolTipLocation(MouseEvent event) {
        if (thisJT == null)
            return null;
        return new Point(this.getWidth() - thisJT.getWidth(),
                this.getHeight() - thisJT.getHeight());
    }

    @Override
    public JToolTip createToolTip() {
        thisJT = super.createToolTip();
        return thisJT;
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

    public void setImage (BufferedImage img) {
        if (image != null)
            stack.push (ImgTools.deepCopy(image));
        image = img;
        String hp = grid.getHistoryPath();
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
