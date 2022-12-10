package thegrid;

import common.Tools;
import database.DBHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageView extends JFrame implements KeyListener {
    private final JScrollPane scrollPane;
    private final java.util.List<DBHandler.NameID> allFiles;
    private final JLabel imgLabel;
    private float scale = 1.0f;
    private String imgPath;
    private int currentIdx;

    private Timer timer = null;

    public ImageView(java.util.List<DBHandler.NameID> files, int idx) {
        allFiles = files;
        currentIdx = idx;
        imgPath = files.get(idx).name;
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
                        "f - save to file<br>"+
                        "t - random image<br>" +
                        "m - mirror</html>");
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private BufferedImage getIconImg() {
        ImageIcon imgIcon = (ImageIcon) (imgLabel.getIcon());
        return Tools.toBufferedImage(imgIcon.getImage());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int ev = e.getKeyCode();
        Tools.fastScroll(ev,scrollPane.getViewport());
        switch (ev) {
            case KeyEvent.VK_PAGE_DOWN -> {
                if (currentIdx < (allFiles.size() - 1))
                    currentIdx++;
                else
                    currentIdx = 0;
                imgPath = allFiles.get(currentIdx).name;
                setImg();
            }
            case KeyEvent.VK_PAGE_UP -> {
                if (currentIdx > 0)
                    currentIdx--;
                else
                    currentIdx = allFiles.size() - 1;
                imgPath = allFiles.get(currentIdx).name;
                setImg();
            }
            case KeyEvent.VK_PLUS -> {
                scale += 0.1f;
                scaleIconImg();
            }
            case KeyEvent.VK_MINUS -> {
                if (scale > 0.2f)
                    scale -= 0.1f;
                scaleIconImg();
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
            case KeyEvent.VK_H -> adjustOnHeight();
            case KeyEvent.VK_T -> {
                currentIdx = (int) (Math.random() * allFiles.size());
                imgPath = allFiles.get(currentIdx).name;
                setTitle(imgPath);
                adjustOnHeight();
            }
            case KeyEvent.VK_S -> {
                if (timer == null) {
                    timer = new Timer(10000, e1 -> {
                        currentIdx = (int) (Math.random() * allFiles.size());
                        imgPath = allFiles.get(currentIdx).name;
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
            case KeyEvent.VK_F -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(allFiles.get(currentIdx)+".jpg"));
                int option = fileChooser.showSaveDialog(this);
                if(option == JFileChooser.APPROVE_OPTION){
                    BufferedImage img = loadImgFromStore();
                    try {
                        ImageIO.write(img, "jpg", fileChooser.getSelectedFile());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            case KeyEvent.VK_L -> setImg();
            case KeyEvent.VK_ESCAPE -> dispose();
        }
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

    private void scaleIconImg() {
        BufferedImage img = loadImgFromStore();
        Dimension dim = new Dimension(
                (int) (img.getWidth(null) * scale),
                (int) (img.getHeight(null) * scale)
        );
        if (dim.height <= 0 || dim.width <= 0 || dim.height > 4000 || dim.width > 4000)
            return;
        img = ImageScaler.scaleExact(img, dim);
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
