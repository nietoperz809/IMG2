package thegrid;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.IApplyInPlace;
import common.ImageScaler;
import common.ImageTools;
import common.Tools;
import database.AccessCounter;
import database.DBHandler;
import dialogs.LineInput;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static common.MsgBox.chooseDir;


public class ImageFrame extends JFrame implements MouseWheelListener {
    protected final ImgPanel imgPanel;
    final UniqueRng indexRing;
    final UniqueRng shuffledRing;
    final TheGrid grid;

    @Override
    public void dispose() {
        super.dispose();
        ImageViewController.remove(this);
    }

    public ImageFrame(TheGrid grid, int idx) {
        this.grid = grid;
        setIconImage(grid.getIconImage());
        shuffledRing = new UniqueRng(grid.imageL.size());
        indexRing = new UniqueRng(grid.imageL.size(), false);
        indexRing.set(idx);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImgViewKeyHandler kh = new ImgViewKeyHandler(this);
        addKeyListener(kh);
        addMouseWheelListener(this);
        BufferedImage img = loadImgFromStore(true);
        assert img != null;
        imgPanel = new ImgPanel(grid, img, this);
        showInfo(null);
        new RegionSelectorListener(img, imgPanel, this);
        setContentPane(imgPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosed(WindowEvent e) {
//                kh.stopTimer();
//            }
//        });
        ImageViewController.add(this);
    }

    BufferedImage getIconImg() {
        return ImageTools.removeAlpha(imgPanel.getImage());
    }

    void saveAsFile(boolean orig) {
        String outPath = chooseDir(this);
        saveImageAsFile(orig, outPath);
    }

    private long imgSavetime;

    /**
     * Save image to disk
     * @param orig true == from database, false == from icon
     * @param outPath pazh where to write to
     */
    public void saveImageAsFile(boolean orig, String outPath) {
        if (outPath != null) {
            int rowid = grid.imageL.get(indexRing.get()).rowid();
            BufferedImage img = orig ? loadImgFromStore(false) : ImageTools.removeAlpha(getIconImg());
            long milli = System.currentTimeMillis(); // prevent dupes
            if (milli - imgSavetime < 500)
                return;
            imgSavetime = milli;
            ImageTools.saveImg2Disk(img, rowid, outPath, null);
        }
    }

    void setNextImage() {
        indexRing.getNext();
        setImg();
        imgPanel.clearOffset();
        adjustOn('h');
    }

    void setPrevImage() {
        indexRing.getPrev();
        setImg();
        imgPanel.clearOffset();
        adjustOn('h');
    }

    FastBitmap getIconAsFastBitmap() {
        return new FastBitmap(getIconImg());
    }

    void applyInplaceFilter(IApplyInPlace bl) {
        FastBitmap fb = getIconAsFastBitmap();
        bl.applyInPlace(fb);
        imgPanel.setImage(fb);
    }

    public void selectAnotherImage(int rowid_in) {
        int n;
        if (rowid_in == -1) {
            String str = LineInput.xmain("?", "Goto:", Color.GREEN,
                    "rowid or 'last/first' keyword", false);
            if (str.startsWith("?")) {
                str = str.substring(1);
            }
            switch (str) {
                case "first":
                    n = 0;
                    break;
                case "last":
                    n = grid.imageL.IndexByRowID(-1);
                    break;
                default:
                    int rowid;
                    try {
                        rowid = Integer.parseInt(str);
                    } catch (NumberFormatException ex) {
                        return;
                    }
                    n = grid.imageL.IndexByRowID(rowid);
                    if (n == -1)
                        return;
            }
        } else {
            n = grid.imageL.IndexByRowID(rowid_in);
        }
        indexRing.set(n);
        adjustOn('h');
        showByIdx();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0)
            imgPanel.scrollDown(imgPanel);
        else
            imgPanel.scrollUp(imgPanel);
    }

    public String toString() {
        return toString (grid.imageL.get(indexRing.get()));
    }

    public String toString(@NotNull DBHandler.NameID v) {
        int len = Integer.parseInt(DBHandler.queryImageLen(v.rowid()));
        BufferedImage bi = loadImgFromStore(false);
        return "IDX:" + indexRing.get() + " ROWID:" +
                v.rowid() + " TAG:" + v.tag() +
                " -- x/y: " + bi.getWidth() + "/" + bi.getHeight() +
                " -- ACC: " + AccessCounter.getAccCounter(v.rowid()) +
                " -- Size: " + Tools.commatize(len) + " Bytes";
    }

    private void showByIdx() {
        setImg();
        adjustOn('h');
    }

    void adjustOn(char which) {
        BufferedImage img = getIconImg();
        assert img != null;
        int newWidth, newHeight;
        if (which == 'h') {
            Insets in = getInsets();
            newHeight = getHeight() - in.top - in.bottom;
            float fact = (float) img.getHeight() / (float) newHeight;
            newWidth = (int) ((float) img.getWidth() / fact);
        } else {
            newWidth = imgPanel.getWidth();
            float fact = (float) img.getWidth() / (float) newWidth;
            newHeight = (int) ((float) img.getHeight() / fact);
        }
        Dimension d = new Dimension(newWidth, newHeight);
        img = common.ImageScaler.scaleDirect(img, d);
        imgPanel.setImageCentered(img);
    }

    private void showInfo(DBHandler.NameID v) {
        String info = v==null ? toString() : toString(v);
        imgPanel.setToolTipText(info);
        setTitle(info);
    }

    void setImg (BufferedImage bimg) {
        imgPanel.setImageCentered(bimg);
//        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        dim.height = min (dim.height, bimg.getHeight());
//        dim.width = min (dim.width, bimg.getWidth());
//        this.setSize(dim);
        showInfo(null);
    }

//    void setImg(int id) {
//        BufferedImage bimg = loadImgFromStore(id, true);
//        imgPanel.setImageCentered(bimg);
//        int index = grid.imageL.IndexByRowID(id);
//        showInfo(grid.imageL.get(index));
//    }

    void setImg() {
        BufferedImage bimg = loadImgFromStore(true);
        setImg (bimg);
    }

    public BufferedImage loadImgFromStore(boolean doInc) {
        int id = grid.imageL.get(indexRing.get()).rowid();
        return loadImgFromStore(id, doInc);
    }

    public BufferedImage loadImgFromStore(int id, boolean doInc) {
        try {
            if (doInc)
                AccessCounter.incAccCounter(id);
            byte[] b = DBHandler.loadImage(id);
            if (b == null) {
                System.out.println("loadImgFromStore-1 fail!!!");
                return TheGrid.failImg;
            }
            BufferedImage b2 = ImageTools.JPGByteArrayToImg(b);
            if (b2 == null) {
                System.out.println("loadImgFromStore-2 fail!!!");
                return TheGrid.failImg;
            }
            return b2;
        } catch (Exception ex) {
            return TheGrid.failImg;
        }
    }

    Point2D.Double scaleIconImg(double factor, boolean up) {
        BufferedImage img = ImageScaler.scaleImg(getIconImg(), factor, up);
        imgPanel.setImage(img);
        return new Point2D.Double(img.getWidth(), img.getHeight());
    }

    Point2D.Double scaleIconImg(boolean up) {
        return scaleIconImg(1.5, up);
    }


    public void zoomIn(Rectangle r) {
        BufferedImage img = ImageTools.crop(getIconImg(), r);
        imgPanel.clearOffset();
        imgPanel.setImage(img);
    }
}
