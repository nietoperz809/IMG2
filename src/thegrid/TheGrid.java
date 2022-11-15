package thegrid;

import imageloader.DBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.Instant;


public class TheGrid extends JFrame {
    //public final String srcDir;
    public final JPanel rootPane;
    public final JScrollPane scrollPane;
    private final java.util.List<String> allFiles;
    private final ProgressBox progress;
    private Instant startTime;
    private int imageCount;

    public TheGrid() {
        allFiles = DBHandler.getInst().getFileNames();
        progress = new ProgressBox(this, allFiles.size());
        rootPane = new JPanel();
        scrollPane = new JScrollPane(rootPane);
        rootPane.setLayout(new GridLayout(0, 10, 2, 2));
        add(scrollPane);
        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        rootPane.setToolTipText(allFiles.size() + " Images, press 'a' to add more");
        new GridListeners(this);
        new GridMenu(this);
    }

    public static void main(String... ignored) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            TheGrid tg = new TheGrid();
            tg.imageCount = 0;
            tg.startTime = Instant.now();

            for (int s = 0; s < tg.allFiles.size(); s++) {
                tg.addImageLabel(s);
            }
        } catch (Exception e) {
            System.err.println("run fail\n" + e);
        }
    }

    public void addImageFilesToDatabase(File[] files) throws Exception {
        DBHandler.getInst().addImages(files, (img, name) -> {
            BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                    new Dimension(100, 100));
            GridImage lab = new GridImage(thumbnailImage, allFiles,
                    rootPane, name);
            rootPane.add(lab);
        });
        rootPane.doLayout();
    }

    /**
     * Add one single image to the frame
     */
    public void addImageLabel(int s) {
        String fileName = allFiles.get(s);
        BufferedImage thumbnailImage = null;
        try {
            thumbnailImage = DBHandler.getInst().loadThumbnail(fileName);
        } catch (Exception e) {
            System.err.println("thumb read fail: " + fileName);
        }
        GridImage lab = new GridImage(thumbnailImage, allFiles, s, rootPane);

        rootPane.add(lab);
        Instant end = Instant.now();
        String txt = "Loaded " + (++imageCount) + " Thumbs in " + Duration.between(startTime, end).toMillis() / 1000 + " Seconds";
        setTitle(txt);
        progress.setTextAndValue(txt, imageCount);
        if (imageCount >= allFiles.size()) {
            progress.dispose();
            rootPane.doLayout();
            scrollPane.getViewport().setView(rootPane);
            setVisible(true);
        }
    }
}


