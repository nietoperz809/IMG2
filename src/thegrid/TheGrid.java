package thegrid;

import buildinfo.BuildInfo;
import common.*;
import database.DBHandler;
import dialogs.ProgressBox;
import thegrid.gridmenu.GridMenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.Instant;

import static common.ImgTools.byteArrayToImg;
import static common.NumToText.*;
import static common.Tools.extractResource;


public class TheGrid extends MyFrame {
    private static int instCount = 0;
    public int thisInstCount;
    public final ImageList imageL = new ImageList();
    public final ImageViewController controller = new ImageViewController();
    public final JPanel rootPane;
    public final JScrollPane scrollPane;
    private final ProgressBox progress;
    private final Instant startTime;
    private int imageCount;
    private boolean stopFill = false;
    private String historyPath = null;

    public String getHistoryPath() {
        return historyPath;
    }

    public void setHistoryPath(String historyPath) {
        System.out.println("histPath: "+historyPath);
        this.historyPath = historyPath;
    }

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


    public TheGrid (String sql, String dbRoot) {
        instCount++;
        thisInstCount = instCount;
        setTitle(dbRoot);
        imageL.setSQL(sql, this);
        //System.out.println("TheGrid constructor called");
        DBHandler.log("Images in DB: "+this.imageL.size());
        progress = new ProgressBox(this, this.imageL.size());
        Win32.dialogToTop(progress);
        rootPane = new JPanel();
        scrollPane = new JScrollPane(rootPane);
        rootPane.setLayout(new GridLayout(0, 8, 1, 1));
        add(scrollPane);
        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        //rootPane.setToolTipText(imageL.size() + " Images, press 'a' to add more");
        new GridListeners(this);
        new GridMenuBar(this);
        // Action ...
        imageCount = 0;
        startTime = Instant.now();

        if (imageL.size() == 0) {
            stopThumbViewFill("sql error");
            return;
        }
        for (int s = 0; s < imageL.size(); s++) {
            if (stopFill)
                break;
            addImageLabel(s);
        }
        this.pack();
    }

    public static void main(String... input) {
//        Thread hook = new Thread(() ->
//                DBHandler.log("SHUTDOWN"));
//        Runtime.getRuntime().addShutdownHook(hook);

        Win32.hideConsoleWindow();

        try {
            boolean askPwd = true;
            String dbRoot = "dbdir:";
            for (String s : input) {
                if (s.startsWith(dbRoot)) {
                    dbRoot = s.substring(dbRoot.length());
                    DBHandler.setDBRoot(dbRoot);
                    //System.out.println(dbRoot);
                }
                else if (s.equals("nopwd")) {
                    askPwd = false;
                }
            }
            if (askPwd)
                Tools.AskforPWD();

            DBHandler.log("+++ TheGrid started");
            new TheGrid (ImageList.mainSQL, dbRoot);
            System.out.println("end main");
        } catch (Exception e) {
            System.out.println("FAIL: " + e);
            DBHandler.log("FAIL: " + e);
        }
    }

    public void addImageFilesToDatabase(File[] files) throws Exception {
        int numadd = DBHandler.MoveImageFilesToDB(files, (img, name) -> {
            BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                    new Dimension(100, 100));
            Thumbnail lab = new Thumbnail(this, thumbnailImage, rootPane, name);
            rootPane.add(lab);
        });
        rootPane.doLayout();
        Sam.speak(convertLessThanOneThousand(numadd)+" new files added");
    }

    public void stopThumbViewFill(String info) {
        stopFill = true;
        progress.dispose();
        rootPane.doLayout();
        scrollPane.getViewport().setView(rootPane);
        if (this.thisInstCount == 1)
            setTitle (getTitle()+ " " + BuildInfo.buildInfo +
                    " -- "+info+" -- H2:"+DBHandler.getH2Version());
        else
            setTitle (imageL.getSql());
        setVisible(true);
        Tools.gc_now();
    }

    /**
     * Add one single image to the frame
     */
    public void addImageLabel(int s) {
        int rowid = imageL.get(s).rowid();
        byte[] thumbBytes = null;
        try {
            thumbBytes = DBHandler.loadThumbnail(rowid);
        } catch (Exception e) {
            System.err.println("thumb read fail: " + rowid);
        }
        Thumbnail lab = new Thumbnail(this, thumbBytes, s, rootPane);

        rootPane.add(lab);
        Instant end = Instant.now();
        String info = "Loaded " + (++imageCount) + " Thumbs in " + Duration.between(startTime, end).toMillis() / 1000 + " Seconds";
        progress.setTextAndValue(info, imageCount);
        if (imageCount >= imageL.size()) {
            stopThumbViewFill(info);
        }
    }
}


