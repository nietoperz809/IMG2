package thegrid;

import common.Tools;
import database.DBHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImageView extends JFrame implements KeyListener {
    private final JScrollPane scrollPane;
    private final java.util.List<DBHandler.NameID> allFiles;
    private final JLabel imgLabel;
    private final UniqueRng ring;
    private String imgPath;
    private int currentIdx;

    private Timer timer = null;

    public ImageView(java.util.List<DBHandler.NameID> files, int idx) {
        ring = new UniqueRng (files.size());
        allFiles = files;
        currentIdx = idx;
        imgPath = files.get(idx).name();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(imgPath);
        addKeyListener(this);
        scrollPane = new JScrollPane();
        imgLabel = new JLabel(new ImageIcon(loadImgFromStore()));
        new RegionSelectorListener(imgLabel, this);
        imgLabel.setBorder(null);
        scrollPane.add(imgLabel);
        scrollPane.getViewport().setView(imgLabel);
        scrollPane.setBorder(null);
        setContentPane(scrollPane);
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
        imgLabel.setToolTipText
                ("<html>+/- - scale<br>" +
                        "r - rotate<br>" +
                        "page up/down - load next/prev image<br>" +
                        "up/down/left/right - move<br>" +
                        "w - scale to width" +
                        "h - scale to height" +
                        "l - reload<br>" +
                        "esc - close window<br>" +
                        "s - slideshow<br>" +
                        "f - save original img to file<br>"+
                        "g - save manipulated img to file<br>"+
                        "c - contrast up<br>"+
                        "x - sharpen<br>"+
                        "v - contrast down<br>"+
                        "t - random image forward<br>" +
                        "z - random image backwardt<br>" +
                        "d - delete from database<br>" +
                        "m - mirror</html>");
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private BufferedImage getIconImg() {
        ImageIcon imgIcon = (ImageIcon) (imgLabel.getIcon());
        return Tools.toBufferedImage(imgIcon.getImage());
    }

    private void sharpenImage() {
        BufferedImage img = getIconImg();

        Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1, -1,
                -1, -1 });
        BufferedImageOp op = new ConvolveOp(kernel);
        img = op.filter(img, null);
        imgLabel.setIcon(new ImageIcon(img));
        repaint();
    }

    private void changeContrast (float val) {
        BufferedImage img = getIconImg();
        RescaleOp op = new RescaleOp (val, 0, null);
        img = op.filter(img, img);
        imgLabel.setIcon(new ImageIcon(img));
        repaint();
    }


    private void saveAsFile (boolean orig) {
        String outPath = Tools.chooseDir(this);
        if (outPath != null) {
            BufferedImage img;
            if (orig)
                img = loadImgFromStore();
            else
                img = Tools.removeAlpha(getIconImg());
            outPath = outPath+File.separator + UUID.randomUUID() + ".jpg";
            System.out.println(outPath);
            try {
                boolean success = ImageIO.write(img, "jpg", new File(outPath));
                if (!success)
                    System.err.println("imgIO write fail ");
            } catch (Exception ex) {
                System.err.println("imgIO write fail "+ex);
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int ev = e.getKeyCode();
        Tools.fastScroll(ev,scrollPane.getViewport(), false);
        switch (ev) {
            case KeyEvent.VK_PAGE_DOWN -> {
                if (currentIdx < (allFiles.size() - 1))
                    currentIdx++;
                else
                    currentIdx = 0;
                imgPath = allFiles.get(currentIdx).name();
                setImg();
            }
            case KeyEvent.VK_PAGE_UP -> {
                if (currentIdx > 0)
                    currentIdx--;
                else
                    currentIdx = allFiles.size() - 1;
                imgPath = allFiles.get(currentIdx).name();
                setImg();
            }
            case KeyEvent.VK_PLUS -> {
                //scale += 0.1f;
                scaleIconImg(1.1f);
            }
            case KeyEvent.VK_MINUS -> {
//                if (scale > 0.2f)
//                    scale -= 0.1f;
                scaleIconImg(0.9f);
            }
            case KeyEvent.VK_R -> {
                BufferedImage img = getIconImg();
                img = Tools.rotateClockwise90(img);
                imgLabel.setIcon(new ImageIcon(img));
                repaint();
            }
            case KeyEvent.VK_M -> {
                BufferedImage img = getIconImg();
                img = Tools.flip(img);
                imgLabel.setIcon(new ImageIcon(img));
                repaint();
            }
            case KeyEvent.VK_W -> {
                BufferedImage img = loadImgFromStore();
                JScrollBar vert = scrollPane.getVerticalScrollBar();
                int newWidth = scrollPane.getWidth();
                if (vert.isVisible())
                    newWidth -= vert.getWidth();
                float fact = (float) img.getWidth() / (float) newWidth;
                int newHeight = (int) ((float) img.getHeight() / fact);
                Dimension d = new Dimension(newWidth, newHeight);
                img = ImageScaler.scaleDirect(img, d);
                imgLabel.setIcon(new ImageIcon(img));
                repaint();
            }
            case KeyEvent.VK_T -> {
                currentIdx = ring.getNext();
                showByIdx();
            }
            case KeyEvent.VK_Z -> {
                currentIdx = ring.getPrev();
                showByIdx();
            }
            case KeyEvent.VK_S -> {
                if (timer == null) {
                    timer = new Timer(10000, e1 -> {
                        currentIdx = ring.getNext();
                        imgPath = allFiles.get(currentIdx).name();
                        setTitle("Slideshow: " + imgPath);
                        adjustOnHeight();
                    });
                    timer.setRepeats(true);
                    timer.setInitialDelay(0);
                    timer.start();
                } else {
                    timer.stop();
                    timer = null;
                    setTitle("Slideshow STOPPED");
                }
            }
            case KeyEvent.VK_D -> DBHandler.getInst().deleteImage(allFiles.get(currentIdx).rowid());
            case KeyEvent.VK_H -> adjustOnHeight();
            case KeyEvent.VK_C -> changeContrast(1.1f);
            case KeyEvent.VK_V -> changeContrast(0.9f);
            case KeyEvent.VK_X -> sharpenImage();
            case KeyEvent.VK_F -> saveAsFile(true);
            case KeyEvent.VK_G -> saveAsFile(false);
            case KeyEvent.VK_L -> setImg();
            case KeyEvent.VK_ESCAPE -> dispose();
        }
    }

    private void showByIdx() {
        imgPath = allFiles.get(currentIdx).name();
        setTitle(imgPath + " -- " + currentIdx);
        adjustOnHeight();
    }

    private void adjustOnHeight() {
        BufferedImage img = loadImgFromStore();
        Insets in = getInsets();
        int newHeight = getHeight() - in.top - in.bottom;
        float fact = (float) img.getHeight() / (float) newHeight;
        int newWidth = (int) ((float) img.getWidth() / fact);
        Dimension d = new Dimension(newWidth, newHeight);
        img = ImageScaler.scaleDirect(img, d);
        imgLabel.setIcon(new ImageIcon(img));
        repaint();
    }

    private void setImg() {
        setTitle(imgPath);
        imgLabel.setIcon(new ImageIcon(loadImgFromStore()));
        repaint();
    }

    private BufferedImage loadImgFromStore() {
        try {
            return DBHandler.getInst().loadImage(imgPath);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void scaleIconImg (float factor) {
        BufferedImage img = ImageScaler.scaleImg(getIconImg(), factor);

        imgLabel.setIcon(new ImageIcon(img));
        repaint();
    }

    public void zoomIn(Rectangle r) {
        BufferedImage img = getIconImg();
        BufferedImage part = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) part.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0,
                img.getWidth(), img.getHeight(),
                r.x, r.y,
                r.x + r.width, r.y + r.height,
                null);
        imgLabel.setIcon(new ImageIcon(part));
        repaint();
    }
}
