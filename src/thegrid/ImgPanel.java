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

import static common.UndoStack.undoStack;

public class ImgPanel extends JPanel implements Positioner {

    private final TheGrid grid;
    private BufferedImage image;

    final ImageFrame theView;

    @Override
    public JToolTip createToolTip() {
        return common.Tools.createCustomToolTip (this);
    }

    public ImgPanel (TheGrid grid, BufferedImage img, ImageFrame parent) {
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
        clearOffset();
        SwingUtilities.invokeLater(this::repaint);
    }

    public void autoSaveImage() {
        String hp = grid.getHistoryPath();
        if (hp != null) {
            SwingUtilities.invokeLater(() -> theView.saveImageAsFile(false, hp));
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void undoImage() {
        BufferedImage img = undoStack.pop();
        if (img != null) {
            image = img;
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    public void setImage(FastBitmap fb) {
        BufferedImage bimg = fb.toBufferedImage();
        setImage(bimg);
    }

    public void setImageCentered(BufferedImage img) {
        setImage(img);
        center (this, new Point2D.Double(img.getWidth(), img.getHeight()));
    }

    public void setImage(BufferedImage img) {
        if (image != null)
            undoStack.push (ImageTools.deepCopy(image));
        image = img;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, (int)offset.x, (int)offset.y, this);
    }

    public void setWatermark(Watermark watermark) {
        if (image != null) {
            undoStack.push (ImageTools.deepCopy(image));
            paintText(image.createGraphics(), watermark.pos, watermark.font,
                    watermark.text, watermark.col, watermark.alpha, watermark.fillground);
            autoSaveImage();
            SwingUtilities.invokeLater(this::repaint);
        }
    }
}
