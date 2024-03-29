package thegrid;

import buildinfo.BuildInfo;
import common.ProgressBox;
import common.Sam;
import common.Tools;
import database.DBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.Instant;

import static common.ImgTools.byteArrayToImg;
import static common.Tools.extractResource;
import static java.util.Objects.*;


public class TheGrid extends MyFrame {
    public final JPanel rootPane;
    public final JScrollPane scrollPane;
    //private java.util.List<DBHandler.NameID> allFiles;
    private final ProgressBox progress;
    private final Instant startTime;
    private int imageCount;
    private boolean stopFill = false;

    public final static BufferedImage failImg;

    static {
        try {
            failImg = byteArrayToImg (extractResource ("fail.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyClick() {
        stopThumbViewFill ("-- prematurely stopped --");
    }

//    public void refresh() {
//        allFiles = requireNonNull(DBHandler.getInst()).getAllImageInfos();
//    }

    public TheGrid (int max) {
        //refresh();
        DBHandler.getInst().log("Images in DB: "+ImageList.size());
        progress = new ProgressBox(this, ImageList.size());
        rootPane = new JPanel();
        scrollPane = new JScrollPane(rootPane);
        rootPane.setLayout(new GridLayout(0, 10, 2, 2));
        add(scrollPane);
        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        rootPane.setToolTipText(ImageList.size() + " Images, press 'a' to add more");
        new GridListeners(this);
        new GridMenu(this);
        // Action ...
        imageCount = 0;
        startTime = Instant.now();

        for (int s = 0; s < ImageList.size(); s++) {
            if (stopFill)
                break;
            addImageLabel(s);
        }
    }

    public static void main(String... input) {
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            int m = -1;
            if (input.length != 0) {
                if (input[0].startsWith("max")) {
                    m = Integer.parseInt(input[0].substring(3));
                }
            }
            Thread hook = new Thread(() ->
                    DBHandler.getInst().log ("SHUTDOWN"));
            Runtime.getRuntime().addShutdownHook(hook);
            DBHandler.getInst().log("+++ TheGrid started");
            new TheGrid(m);
        } catch (Exception e) {
            DBHandler.getInst().log ("FAIL: "+e.toString());
        }
    }

    public void addImageFilesToDatabase(File[] files) throws Exception {
        int numadd = requireNonNull(DBHandler.getInst()).MoveImageFilesToDB(files, (img, name) -> {
            BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                    new Dimension(100, 100));
            GridImage lab = new GridImage(thumbnailImage, rootPane, name);
            rootPane.add(lab);
        });
        rootPane.doLayout();
        Sam.speak(numadd+"New files added");
    }

    public void stopThumbViewFill(String info) {
        stopFill = true;
        progress.dispose();
        rootPane.doLayout();
        scrollPane.getViewport().setView(rootPane);
        setTitle (BuildInfo.buildInfo + " -- " + info);
        setVisible(true);
        Tools.gc_now();
    }

    /**
     * Add one single image to the frame
     */
    public void addImageLabel(int s) {
        String fileName = ImageList.get(s).name();
        DBHandler.ThumbHash tbh = null;
        try {
            tbh = DBHandler.getInst().loadThumbnail(fileName);
        } catch (Exception e) {
            System.err.println("thumb read fail: " + fileName);
        }
        GridImage lab = new GridImage(tbh, s, rootPane);

        rootPane.add(lab);
        Instant end = Instant.now();
        String info = "Loaded " + (++imageCount) + " Thumbs in " + Duration.between(startTime, end).toMillis() / 1000 + " Seconds";
        progress.setTextAndValue(info, imageCount);
        if (imageCount >= ImageList.size()) {
            stopThumbViewFill(info);
        }
    }
}


