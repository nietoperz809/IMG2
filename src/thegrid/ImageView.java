package thegrid;

import common.ImgTools;
import common.LineInput;
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
    private int currentIdx;

    private Timer timer = null;

    public ImageView(java.util.List<DBHandler.NameID> files, int idx) {
        ring = new UniqueRng (files.size());
        allFiles = files;
        currentIdx = idx;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(allFiles.get(currentIdx).name() + " -- " + currentIdx+ " -- " + allFiles.get(currentIdx).rowid());
        addKeyListener(this);
        scrollPane = new JScrollPane();
        BufferedImage img = loadImgFromStore();
        ImageIcon icon = new ImageIcon(img);
        imgLabel = new JLabel(icon);
        new RegionSelectorListener(img,imgLabel, this);
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
                        "1,2 - gamma<br>" +
                        "3,4 - change contrast<br>"+
                        "a - tagger<br>" +
                        "ctrl+c - copy to clipboard<br>" +
                        "r - rotate<br>" +
                        "c - change img in database<br>" +
                        "n - go to specific rowid<br>" +
                        "page up/down - load next/prev image<br>" +
                        "up/down/left/right - move<br>" +
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

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private BufferedImage getIconImg() {
        ImageIcon imgIcon = (ImageIcon) (imgLabel.getIcon());
        return ImgTools.toBufferedImage(imgIcon.getImage());
    }


    private void sharpenImage() {
        BufferedImage img = ImgTools.sharpenImage(getIconImg());
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
                img = ImgTools.removeAlpha(getIconImg());
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
                setImg();
            }
            case KeyEvent.VK_PAGE_UP -> {
                if (currentIdx > 0)
                    currentIdx--;
                else
                    currentIdx = allFiles.size() - 1;
                setImg();
            }
            case KeyEvent.VK_PLUS -> scaleIconImg(1.1f);
            case KeyEvent.VK_MINUS -> scaleIconImg(0.9f);
            case KeyEvent.VK_R -> {
                BufferedImage img = getIconImg();
                img = ImgTools.rotateClockwise90(img);
                imgLabel.setIcon(new ImageIcon(img));
                repaint();
            }
            case KeyEvent.VK_M -> {
                BufferedImage img = getIconImg();
                img = ImgTools.flip(img);
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
                imgLabel.setIcon(new ImageIcon(img));
                repaint();
            }
            case KeyEvent.VK_2 -> {
                BufferedImage img = getIconImg();
                img = ImgTools.gammaCorrection(img, 1f/0.7f);
                imgLabel.setIcon(new ImageIcon(img));
                repaint();
            }
            case KeyEvent.VK_D -> DBHandler.getInst().deleteImage(allFiles.get(currentIdx).rowid());
            case KeyEvent.VK_H -> adjustOnHeight();
            case KeyEvent.VK_3 -> changeContrast(1.1f);
            case KeyEvent.VK_4 -> changeContrast(0.9f);
            case KeyEvent.VK_X -> sharpenImage();
            case KeyEvent.VK_F -> saveAsFile(true);
            case KeyEvent.VK_G -> saveAsFile(false);
            case KeyEvent.VK_L -> setImg();

            case KeyEvent.VK_ESCAPE -> dispose();
            case KeyEvent.VK_A -> {
                int rowid = allFiles.get(currentIdx).rowid();
                String tag = LineInput.xmain(DBHandler.getInst().getTag(rowid), "Tag:").trim().toLowerCase();
                DBHandler.getInst().setTag(rowid, tag);
            }
            case KeyEvent.VK_N -> {
                String str = LineInput.xmain("?", "Goto:");
                int rowid = Integer.parseInt(str);
                currentIdx = seekRowid (rowid);
                showByIdx();
            }
            case KeyEvent.VK_C -> {
                if (e.isControlDown()) {
                    BufferedImage img = getIconImg();
                    ImgTools.copyImage(img);
                } else {
                    int id = allFiles.get(currentIdx).rowid();
                    if (Tools.Question("Replace image #" + id)) {
                        BufferedImage img = getIconImg();
                        DBHandler.getInst().changeBigImg(img, id);
                    }
                }
            }
        }
    }

    private int seekRowid (int rowid) {
        for (int n=0; n<allFiles.size(); n++) {
            if (allFiles.get(n).rowid() == rowid) {
                return n;
            }
        }
        throw new RuntimeException("rowid not found in list: "+rowid);
    }

    private void showByIdx() {
        setTitle(allFiles.get(currentIdx).name() + " -- " + currentIdx+ " -- " + allFiles.get(currentIdx).rowid());
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
        setTitle(allFiles.get(currentIdx).name() + " -- " + currentIdx+ " -- " + allFiles.get(currentIdx).rowid());
        imgLabel.setIcon(new ImageIcon(loadImgFromStore()));
        repaint();
    }

    private BufferedImage loadImgFromStore() {
        try {
            byte[] b = DBHandler.getInst().loadImage(allFiles.get(currentIdx).rowid());
            if (b == null) {
                System.out.println("loadImgFromStore-1 fail!!!");
                return null;
            }
            BufferedImage b2 = ImgTools.byteArrayToImg (b);
            if (b2 == null) {
                System.out.println("loadImgFromStore-2 fail!!!");
                return null;
            }
            return b2;
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
        BufferedImage img = ImgTools.zoomIn(getIconImg(), r);
        imgLabel.setIcon(new ImageIcon(img));
        repaint();
    }
}
