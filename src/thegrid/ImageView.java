package thegrid;

import common.*;
import database.DBHandler;
import common.Denoise;
import jhlabs.image.DiffuseFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static thegrid.ImageList.IndexByRowID;


public class ImageView extends JFrame implements KeyListener, MouseWheelListener {
    private final ImgPanel imgPanel;
    private final UniqueRng ring;
    private int currentIdx;

    private Timer timer = null;

    public ImageView (int idx) {
        ring = new UniqueRng (ImageList.size());
        //allFiles = files;
        currentIdx = idx;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(ImageList.get(currentIdx).name() + " -- " + currentIdx+ " -- " + ImageList.get(currentIdx).rowid());
        addKeyListener(this);
        addMouseWheelListener(this);
        BufferedImage img = loadImgFromStore();
        assert img != null;
        imgPanel = new ImgPanel(img);
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
        if (outPath != null) {
            BufferedImage img;
            if (orig)
                img = loadImgFromStore();
            else
                img = ImgTools.removeAlpha(getIconImg());
            outPath = outPath+File.separator + UUID.randomUUID() + ".jpg";
            System.out.println(outPath);
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

    volatile boolean inEvent = false;

    @Override
    public void keyPressed(KeyEvent e) {
        if (inEvent)
            return;
        inEvent = true;
        InternalKeyPressed(e);
        inEvent = false;
    }

    private void setNextImage() {
        if (currentIdx < (ImageList.size() - 1))
            currentIdx++;
        else
            currentIdx = 0;
        imgPanel.clearOffset();
        setImg();
    }

    private void setBeforeImage() {
        if (currentIdx > 0)
            currentIdx--;
        else
            currentIdx = ImageList.size() - 1;
        imgPanel.clearOffset();
        setImg();
    }

    private void InternalKeyPressed(KeyEvent e) {
        int ev = e.getKeyCode();
        switch (ev) {
            case KeyEvent.VK_PAGE_DOWN -> setNextImage();
            case KeyEvent.VK_PAGE_UP -> setBeforeImage();
            case KeyEvent.VK_PLUS -> scaleIconImg(1.1f);
            case KeyEvent.VK_MINUS -> scaleIconImg(0.9f);
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
                BufferedImage img = getIconImg();
                int newWidth = imgPanel.getWidth();
                assert img != null;
                float fact = (float) img.getWidth() / (float) newWidth;
                int newHeight = (int) ((float) img.getHeight() / fact);
                Dimension d = new Dimension(newWidth, newHeight);
                img = ImageScaler.scaleDirect(img, d);
                imgPanel.setImage(img);
            }
            case KeyEvent.VK_T -> {
                currentIdx = ring.getNext();
                imgPanel.clearOffset();
                showByIdx();
            }
            case KeyEvent.VK_Z -> {
                if (e.isControlDown()) {
                    imgPanel.undo();
                    return;
                }
                currentIdx = ring.getPrev();
                imgPanel.clearOffset();
                showByIdx();
            }
            case KeyEvent.VK_S -> {
                if (timer == null) {
                    timer = new Timer(10000, e1 -> {
                        currentIdx = ring.getNext();
                        setTitle("Slideshow: " + getTitle());
                        adjustOnHeight();
                    });
                    timer.setRepeats(true);
                    timer.setInitialDelay(0);
                    timer.start();
                } else {
                    timer.stop();
                    timer = null;
                    setTitle("Slideshow STOPPED "+ getTitle());
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
                    Objects.requireNonNull(DBHandler.getInst()).deleteImage(ImageList.get(currentIdx).rowid());
                }
            }
            case KeyEvent.VK_H -> {
                imgPanel.clearOffset();
                adjustOnHeight();
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
                int rowid = ImageList.get(currentIdx).rowid();
                String tag = LineInput.xmain(
                        Objects.requireNonNull(DBHandler.getInst()).getTag(rowid), "Tag:", Color.YELLOW)
                        .trim().toLowerCase();
                DBHandler.getInst().setTag(rowid, tag);
            }

            case KeyEvent.VK_5 -> {
                BufferedImage img = getIconImg();
                Denoise d = new Denoise(img);
                BufferedImage out = d.perform_denoise();
                imgPanel.setImage(out);
            }

            case KeyEvent.VK_6 -> {
                BufferedImage img = getIconImg();
                DiffuseFilter df = new DiffuseFilter();
                BufferedImage out = df.filter(img, null);
                imgPanel.setImage(out);
            }

            case KeyEvent.VK_N -> selectAnotherImage();

            case KeyEvent.VK_C -> {
                if (e.isControlDown()) {
                    BufferedImage img = getIconImg();
                    ImgTools.imageToClipboard(img);
                } else {
                    int id = ImageList.get(currentIdx).rowid();
                    if (Tools.Question("Replace image #" + id)) {
                        BufferedImage img = getIconImg();
                        Objects.requireNonNull(DBHandler.getInst()).changeBigImg(img, id);
                    }
                }
            }
            case KeyEvent.VK_UP -> imgPanel.scrollDown();
            case KeyEvent.VK_DOWN -> imgPanel.scrollUp();
            case KeyEvent.VK_LEFT -> imgPanel.scrollRight();
            case KeyEvent.VK_RIGHT -> imgPanel.scrollLeft();
            case KeyEvent.VK_CONTROL -> {}

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
                n = IndexByRowID(-1);
                break;
            default:
                int rowid;
                try {
                    rowid = Integer.parseInt(str);
                } catch (NumberFormatException ex) {
                    return;
                }
                n = IndexByRowID(rowid);
                if (n == -1)
                    return;
        }
        currentIdx = n;
        showByIdx();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0)
            imgPanel.scrollDown();
        else
            imgPanel.scrollUp();
    }


    private void showByIdx() {
        setTitle(ImageList.get(currentIdx).name() + " -- " + currentIdx+ " -- " + ImageList.get(currentIdx).rowid());
        adjustOnHeight();
    }

    private void adjustOnHeight() {
        BufferedImage img = loadImgFromStore();
        Insets in = getInsets();
        int newHeight = getHeight() - in.top - in.bottom;
        assert img != null;
        float fact = (float) img.getHeight() / (float) newHeight;
        int newWidth = (int) ((float) img.getWidth() / fact);
        Dimension d = new Dimension(newWidth, newHeight);
        img = ImageScaler.scaleDirect(img, d);
        imgPanel.setImage(img);
    }

    private void setImg() {
        DBHandler.NameID dbh = ImageList.get(currentIdx);
        setTitle(dbh.name() + " -- " + currentIdx+ " -- " + dbh.rowid());
        imgPanel.setImage(loadImgFromStore());
    }

    private BufferedImage loadImgFromStore() {
        try {
            byte[] b = Objects.requireNonNull(DBHandler.getInst()).loadImage(ImageList.get(currentIdx).rowid());
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
