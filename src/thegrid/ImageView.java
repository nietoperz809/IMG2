package thegrid;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Artistic.HeatMap;
import Catalano.Imaging.Filters.Artistic.OilPainting;
import Catalano.Imaging.Filters.Artistic.SpecularBloom;
import Catalano.Imaging.Filters.Dilatation;
import Catalano.Imaging.Filters.Erosion;
import Catalano.Imaging.Filters.FastVariance;
import Catalano.Imaging.Filters.HistogramEqualization;
import Catalano.Imaging.IApplyInPlace;
import common.*;
import database.DBHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class ImageView extends JFrame implements MouseWheelListener {
    private final ImgPanel imgPanel;
    private final UniqueRng ring2;
    private final UniqueRng shuffledRing;
    private final TheGrid grid;

    private Timer timer = null;

    @Override
    public void dispose() {
        super.dispose();
        grid.controller.remove (this);
    }

    class KA extends KeyAdapter {
        private long keyTime;

        private boolean slowDownKeyEvents() {
            long t = System.currentTimeMillis();
            long diff = t - keyTime;
            if (diff < 300)
                return false;
            else
                keyTime = t;
            return true;
        }

        public void keyPressed (KeyEvent e) {
            int ev = e.getKeyCode();
            switch (ev) {
                case KeyEvent.VK_UP -> {
                    imgPanel.scrollDown();
                    return;
                }
                case KeyEvent.VK_DOWN -> {
                    imgPanel.scrollUp();
                    return;
                }
                case KeyEvent.VK_LEFT -> {
                    imgPanel.scrollRight();
                    return;
                }
                case KeyEvent.VK_RIGHT -> {
                    imgPanel.scrollLeft();
                    return;
                }
                case KeyEvent.VK_CONTROL -> {
                    return;
                }
            }
            if (!slowDownKeyEvents())
                return;
            switch (ev) {
                case KeyEvent.VK_PAGE_DOWN -> setNextImage();
                case KeyEvent.VK_PAGE_UP -> setBeforeImage();
                case KeyEvent.VK_PLUS -> scaleIconImg(1.5f);
                case KeyEvent.VK_MINUS -> scaleIconImg(0.9f);

                case KeyEvent.VK_J -> {
                    int id = grid.imageL.get(ring2.get()).rowid();
                    String init = ""+DBHandler.getInst().getAccCounter(id);
                    int res = LineInput.onlyPosNumber(init, "new acc counter for: "+id,
                    Color.orange);
                    DBHandler.getInst().setAccCounter(id, res);
                }

                case KeyEvent.VK_R -> {
                    BufferedImage img = getIconImg();
                    img = ImgTools.rotateClockwise90(img);
                    imgPanel.setImage(img);
                }

                case KeyEvent.VK_M -> {
                    BufferedImage img = getIconImg();
                    img = ImgTools.flip(img);
                    imgPanel.setImage(img);
                }

                case KeyEvent.VK_W -> {
                    imgPanel.clearOffset();
                    adjustOn('w');
                }

                case KeyEvent.VK_T -> {
                    ring2.set(shuffledRing.getNext());
                    setImg();
                    imgPanel.clearOffset();
                    adjustOn('h');
                }

                case KeyEvent.VK_Z -> {
                    if (e.isControlDown()) {
                        imgPanel.undo();
                        return;
                    }
                    ring2.set(shuffledRing.getPrev());
                    setImg();
                    imgPanel.clearOffset();
                    adjustOn('h');
                }

                case KeyEvent.VK_S -> {
                    if (timer == null) {
                        timer = new Timer(10000, e1 -> {
                            ring2.set(shuffledRing.getNext());
                            setImg();
                            imgPanel.clearOffset();
                            adjustOn('h');
                        });
                        timer.setRepeats(true);
                        timer.setInitialDelay(0);
                        timer.start();
                    } else {
                        timer.stop();
                        timer = null;
                        setTitle("Slideshow STOPPED "+ ImageView.this);
                    }
                }

                case KeyEvent.VK_1 -> {
                    BufferedImage img = getIconImg();
                    img = ImgTools.gammaCorrection(img, 0.7f);
                    imgPanel.setImage(img);
                }

                case KeyEvent.VK_2 -> {
                    BufferedImage img = getIconImg();
                    img = ImgTools.gammaCorrection(img, 1f/0.7f);
                    imgPanel.setImage(img);
                }

                case KeyEvent.VK_D -> {
                    if (Tools.Question("Delete image from DB?")) {
                        Objects.requireNonNull(DBHandler.getInst()).deleteImage(grid.imageL.get(ring2.get()).rowid());
                    }
                }

                case KeyEvent.VK_H -> {
                    imgPanel.clearOffset();
                    adjustOn('h');
                }

                case KeyEvent.VK_3 -> changeContrast(1.1f);
                case KeyEvent.VK_4 -> changeContrast(0.9f);
                case KeyEvent.VK_X -> sharpenImage();
                case KeyEvent.VK_F -> saveAsFile(true);
                case KeyEvent.VK_G -> saveAsFile(false);

                case KeyEvent.VK_L -> {
                    imgPanel.clearOffset();
                    setImg();
                }

                case KeyEvent.VK_ESCAPE -> dispose();

                case KeyEvent.VK_A -> {
                    int rowid = grid.imageL.get(ring2.get()).rowid();
                    String tag = LineInput.xmain(
                                    Objects.requireNonNull(DBHandler.getInst()).getTag(rowid), "Tag:", Color.YELLOW)
                            .trim().toLowerCase();
                    DBHandler.getInst().setTag(rowid, tag);
                }

                case KeyEvent.VK_5 -> { // denoise
                    BufferedImage img = getIconImg();
                    Denoise d = new Denoise(img);
                    BufferedImage out = d.perform_denoise();
                    imgPanel.setImage(out);
                }

                case KeyEvent.VK_6 -> applyInplaceFilter(new Dilatation());
                case KeyEvent.VK_7 -> applyInplaceFilter(new OilPainting());
                case KeyEvent.VK_8 -> applyInplaceFilter(new Erosion());
                case KeyEvent.VK_9 -> applyInplaceFilter(new SpecularBloom());
                case KeyEvent.VK_0 -> applyInplaceFilter(new HistogramEqualization());
                case KeyEvent.VK_B -> applyInplaceFilter(new FastVariance());

                case KeyEvent.VK_Y -> {   //  heatmap
                    FastBitmap fb = IconToFastBitmap();
                    HeatMap bl = new HeatMap();
                    if (e.isControlDown())
                        bl.setInvert(true);
                    bl.applyInPlace(fb);
                    imgPanel.setImage(fb);
                }

                case KeyEvent.VK_N -> selectAnotherImage();

                case KeyEvent.VK_C -> {
                    if (e.isControlDown()) {
                        BufferedImage img = getIconImg();
                        ImgTools.imageToClipboard(img);
                    } else {
                        int id = grid.imageL.get(ring2.get()).rowid();
                        if (Tools.Question("Replace image #" + id)) {
                            BufferedImage img = getIconImg();
                            Objects.requireNonNull(DBHandler.getInst()).changeBigImg(img, id);
                        }
                    }
                }

                case KeyEvent.VK_I -> {
                    String name = "?";
                    name = LineInput.xmain(name, "New Entry:", Color.RED);
                    if (name.equals("?") || name.isEmpty())
                        return;
                    BufferedImage img = getIconImg();
                    try {
                        DBHandler.getInst().insertImageRecord(name, img);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                default -> Sam.speak("Key not used.");
            }
        }
    }


    public ImageView (TheGrid grid, int idx) {
        this.grid = grid;
        shuffledRing = new UniqueRng (grid.imageL.size());
        ring2 = new UniqueRng (grid.imageL.size(), false);
        ring2.set (idx);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addKeyListener(new KA());
        addMouseWheelListener(this);
        BufferedImage img = loadImgFromStore();
        assert img != null;
        imgPanel = new ImgPanel(grid,img, this);
        setTitle(toString());
        new RegionSelectorListener(img, imgPanel, this);
        setContentPane(imgPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
            }
        });
    }

    private BufferedImage getIconImg() {
        return imgPanel.getImage();
    }


    private void sharpenImage() {
        BufferedImage img = ImgTools.sharpenImage(getIconImg());
        imgPanel.setImage(img);
    }

    private void changeContrast (float val) {
        BufferedImage img = getIconImg();
        RescaleOp op = new RescaleOp (val, 0, null);
        img = op.filter(img, img);
        imgPanel.setImage(img);
    }


    private void saveAsFile (boolean orig) {
        String outPath = Tools.chooseDir(this);
        saveImageAsFile (orig, outPath);
    }


    private long imgSavetime;

    public void saveImageAsFile (boolean orig, String outPath) {
        if (outPath != null) {
            int rowid = grid.imageL.get(ring2.get()).rowid();
            BufferedImage img;

            if (orig)
                img = loadImgFromStore();
            else
                img = ImgTools.removeAlpha(getIconImg());

            long milli = System.currentTimeMillis(); // prevent dupes
            if (milli - imgSavetime < 500)
                return;
            imgSavetime = milli;

            outPath = outPath+File.separator +
                    rowid + "-" + milli
                    + ".jpg";
            try {
                assert img != null;
                boolean success = ImageIO.write(img, "jpg", new File(outPath));
                if (!success)
                    System.err.println("imgIO write fail ");
            } catch (Exception ex) {
                System.err.println("imgIO write fail "+ex);
                throw new RuntimeException(ex);
            }
        }
    }

    private void setNextImage() {
        ring2.getNext();
        setImg();
        imgPanel.clearOffset();
        adjustOn('h');
    }

    private void setBeforeImage() {
        ring2.getPrev();
        setImg();
        imgPanel.clearOffset();
        adjustOn('h');
    }

    private FastBitmap IconToFastBitmap() {
        return new FastBitmap(getIconImg());
    }

    private void applyInplaceFilter (IApplyInPlace bl) {
        FastBitmap fb = IconToFastBitmap();
        bl.applyInPlace(fb);
        imgPanel.setImage(fb);
    }

    public void selectAnotherImage() {
        String str = LineInput.xmain("?", "Goto:", Color.GREEN,
                "rowid or 'last/first' keyword");
        if (str.startsWith("?")) {
            str = str.substring(1);
        }
        int n;
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
        ring2.set(n);
        showByIdx();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0)
            imgPanel.scrollDown();
        else
            imgPanel.scrollUp();
    }

    public String toString() {
        var v = grid.imageL.get(ring2.get());
        return "IDX:" + ring2.get() + " ROWID:" +
                v.rowid() + " TAG:" + v.tag() +
                " -- ACC: "+DBHandler.getInst().getAccCounter(v.rowid());
    }

    private void showByIdx() {
        adjustOn('h');
        setImg();
    }

    private void adjustOn(char which) {
        BufferedImage img = getIconImg();
        assert img != null;
        int newWidth, newHeight;
        if (which == 'h') {
            Insets in = getInsets();
            newHeight = getHeight() - in.top - in.bottom;
            float fact = (float) img.getHeight() / (float) newHeight;
            newWidth = (int) ((float) img.getWidth() / fact);
        }
        else {
            newWidth = imgPanel.getWidth();
            float fact = (float) img.getWidth() / (float) newWidth;
            newHeight = (int) ((float) img.getHeight() / fact);
        }
        Dimension d = new Dimension(newWidth, newHeight);
        img = ImageScaler.scaleDirect(img, d);
        imgPanel.setImage(img);
    }

    private void setImg() {
        BufferedImage bimg = loadImgFromStore();
        imgPanel.setImage(bimg);
        setTitle(toString());
    }

    private BufferedImage loadImgFromStore() {
        try {
            var v = DBHandler.getInst();
            int id = grid.imageL.get(ring2.get()).rowid();
            v.incAccCounter(id);
            System.out.println("accC:"+v.getAccCounter(id));
            byte[] b = Objects.requireNonNull(v).loadImage(id);
            if (b == null) {
                System.out.println("loadImgFromStore-1 fail!!!");
                return TheGrid.failImg;
            }
            BufferedImage b2 = ImgTools.byteArrayToImg (b);
            if (b2 == null) {
                System.out.println("loadImgFromStore-2 fail!!!");
                return TheGrid.failImg;
            }
            return b2;
        } catch (Exception ex) {
            return TheGrid.failImg;
        }
    }

    private void scaleIconImg (float factor) {
        BufferedImage img = ImageScaler.scaleImg(getIconImg(), factor);
        imgPanel.setImage(img);
    }

    public void zoomIn(Rectangle r) {
        BufferedImage img = ImgTools.crop(getIconImg(), r);
        imgPanel.clearOffset();
        imgPanel.setImage(img);
    }
}
