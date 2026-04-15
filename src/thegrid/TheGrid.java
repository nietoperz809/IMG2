package thegrid;

import buildinfo.BuildInfo2;
import common.*;
import database.DBHandler;
import dialogs.ProgressBox;
import thegrid.gridmenu.GridMenuBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.Instant;

import static buildinfo.BuildInfo2.GIT_REV;
import static common.ImageTools.JPGByteArrayToImg;
import static common.MsgBox.AskforPWD;
import static common.NumToText.convertLessThanOneThousand;
import static common.Tools.delay;
import static common.Tools.extractResource;
import static database.SqlListFunctions.putQuery;
import static java.awt.event.KeyEvent.*;


public class TheGrid extends MyFrame {
    private static int instCount = 0;
    private static TheGrid mainGrid;
    // static ImageView mainView;
    public int thisInstCount;
    public final ImageList imageL = new ImageList();
    public final JPanel rootPane;
    public final JScrollPane scrollPane;
    private ProgressBox progress;
    private Instant startTime;
    private int imageCount;
    private boolean stopFill = false;
    private String historyPath = null;

    public static TheGrid getMainGrid() {
        return mainGrid;
    }

    public String getHistoryPath() {
        return historyPath;
    }

    public void setHistoryPath(String historyPath) {
        System.out.println("histPath: " + historyPath);
        this.historyPath = historyPath;
    }

    public final static BufferedImage failImg;

    static {
        try {
            failImg = JPGByteArrayToImg(extractResource("fail.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyClick() {
        stopThumbViewFill("-- prematurely stopped --");
    }


    public TheGrid(String sql, String dbRoot) {
        if (instCount == 0)
            mainGrid = this;
        instCount++;
        thisInstCount = instCount;
        try {
            putQuery(sql);
        } catch (Exception e) {
            System.out.println("sql already stored");
        }
        setTitle(dbRoot);
        try {
            setIconImage(JPGByteArrayToImg(extractResource("favicon.ico")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        imageL.setSQL(sql);
        DBHandler.log("Images in DB: " + this.imageL.size());
        rootPane = new JPanel();
        scrollPane = new JScrollPane(rootPane);
        rootPane.setLayout(new GridLayout(0, 8, 1, 1));
        add(scrollPane);
        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        new GridListeners(this);
        new GridMenuBar(this);
        // Action ...
        if (thisInstCount > 1) {
            ImageViewController.add(this);
        }
        if (imageL.size() == 0) {
            stopThumbViewFill("sql error");
            return;
        }
        fillThumbs (new UnrelatedPair<>(0,imageL.size())); // Load initial thumbs
    }

    public void fillThumbs (UnrelatedPair<Integer> pair) {
        //final int lim = imageL.size();
        imageCount = 0;
        final int maxlen = Math.max((pair.second - pair.first), 100);
        startTime = Instant.now();
        Tools.runTask(() -> {
            progress = new ProgressBox(TheGrid.this, maxlen);
            imageL.load();
            rootPane.removeAll();
            stopFill = false;
            for (int s = pair.first; s < pair.second ; s++) {
                //System.out.println(pair.first+ " - "+pair.second+ " - "+ s);
                if (stopFill)
                    break;
                addThumbnail(s);
            }
            pack();
            this.setVisible(true);
            progress.dispose();
        });
    }

//    public void fillThumbs (int limit) {
//        final int lim = limit == -1 ? imageL.size() : limit;
//        Tools.runTask(() -> {
//            imageCount = 0;
//            startTime = Instant.now();
//            progress = new ProgressBox(TheGrid.this, lim);
//            imageL.load();
//            rootPane.removeAll();
//            stopFill = false;
//            for (int s = 0; s < lim ; s++) {
//                if (stopFill)
//                    break;
//                addThumbnail(s);
//            }
//            pack();
//            progress.dispose();
//        });
//    }

    public static void main(String... input) throws Exception {
//        Thread hook = new Thread(() ->
//                DBHandler.log("SHUTDOWN"));
//        Runtime.getRuntime().addShutdownHook(hook);

        ImageIO.scanForPlugins();

        // move shift, alt, win up
        Robot r = new Robot();
        r.keyRelease(VK_SHIFT);
        r.keyRelease(VK_CONTROL);
        r.keyRelease(VK_CAPS_LOCK);
        r.keyRelease(VK_WINDOWS);

        Thread.setDefaultUncaughtExceptionHandler((_, e) -> {
            try {
                StringBuilder builder = new StringBuilder ();
                builder.append("--- ").append(e).append(" ---\n");
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    builder.append(stackTraceElement.toString());
                    builder.append("\n");
                }
                MsgBox.Error (builder.toString());
            } catch (Exception ex) {
                MsgBox.Error ("Exception in DefaultUncaughtExceptionHandler!!!");
            }
        });

        UIManager.put("ToolTip.font", new Font("Arial", Font.BOLD, 20));

        boolean askPwd = true;
        String dbRoot = "dbdir:";
        for (String s : input) {
            if (s.startsWith(dbRoot)) {
                dbRoot = s.substring(dbRoot.length());
                DBHandler.startDatabase(dbRoot);
            } else if (s.equals("nopwd")) {
                askPwd = false;
            }
        }
        if (askPwd) {
            AskforPWD();
        }

        DBHandler.log("+++ TheGrid started");
        new TheGrid(ImageList.mainSQL, dbRoot);
        System.out.println("end main");
        delay(1000);
        Win32.hideConsoleWindow(true);
   }

    public void addImageFilesToDatabase(File[] files) throws Exception {
        int numadd = DBHandler.MoveImageFilesToDB(files, (img) -> {
            BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                    new Dimension(100, 100));
            Thumbnail lab = new Thumbnail(this, thumbnailImage, rootPane);
            rootPane.add(lab);
        });
        rootPane.doLayout();
        Sam.speak(convertLessThanOneThousand(numadd) + " new files added");
    }

    public void stopThumbViewFill(String info) {
        stopFill = true;
        progress.dispose();
        rootPane.doLayout();
        scrollPane.getViewport().setView(rootPane);
        setVisible(true);
        Tools.gc_now();
    }

    void setFinalTitle(String info) {
        if (this.thisInstCount == 1)
            setTitle(getTitle() + " -- " + BuildInfo2.BUILD_NUMBER + " -- " +
                    BuildInfo2.BUILD_DATE +
                    " -- Git:" + GIT_REV +
                    " -- H2:" + DBHandler.getH2Version() +
                    " -- " + info);
        else
            setTitle(imageL.getSql());
    }

    /**
     * Add one single image to the frame
     */
    public void addThumbnail(int s) {
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
        String info = "Loaded " + (++imageCount) + " Thumbs in " +
                Duration.between(startTime, end).toSeconds() + " Seconds";
        progress.setTextAndValue(info, imageCount);
        if (imageCount >= 100) {
            stopThumbViewFill(info);
            setFinalTitle(info);
        }
    }
}


