package thegrid;

import buildinfo.BuildInfo;
import com.sun.jna.platform.win32.DBT;
import common.ProgressBox;
import common.Sam;
import common.Tools;
import database.DBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static common.ImgTools.byteArrayToImg;
import static common.Tools.extractResource;
import static java.util.Objects.*;


public class TheGrid extends MyFrame {
    public static TheGrid instance;
    public final ImageViewController controller = new ImageViewController();
    public final JPanel rootPane;
    public final JScrollPane scrollPane;
    //private java.util.List<DBHandler.NameID> allFiles;
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

//    public void refresh() {
//        allFiles = requireNonNull(DBHandler.getInst()).getAllImageInfos();
//    }

    public TheGrid (int max) {
        System.out.println("TheGrid constructor called");
        instance = this;
        DBHandler.getInst().log("Images in DB: "+ImageList.size());
        progress = new ProgressBox(this, ImageList.size());
        rootPane = new JPanel();
        scrollPane = new JScrollPane(rootPane);
        rootPane.setLayout(new GridLayout(0, 10, 1, 1));
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

        if (ImageList.size() == 0) {
            stopThumbViewFill("sql error");
            return;
        }
        for (int s = 0; s < ImageList.size(); s++) {
            if (stopFill)
                break;
            addImageLabel(s);
        }
    }

    public static void main(String... input) {
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //int m = -1;
            if (input.length != 0) {
                if (input[0].startsWith("dbdir:")) {
                    String dir = input[0].substring(6);
                    DBHandler.getInst().setDBRoot(dir);
                    System.out.println(dir);
                }
            }
            Thread hook = new Thread(() ->
                    DBHandler.getInst().log("SHUTDOWN"));
            Runtime.getRuntime().addShutdownHook(hook);

            /**************************************************/
//            DBHandler db = DBHandler.getInst();
//            int rid = 2;
//            db.incAccCounter(rid);
//            int a1 = db.getAccCounter(rid);
//            if (a1 == 0)
//            {
//                db.setAccCounter (rid, 1);
//                a1 = db.getAccCounter(rid);
//            }
//            System.out.println(a1);
//            System.exit(0);
            /**************************************************/

            DBHandler.getInst().log("+++ TheGrid started");
            new TheGrid(-1);
        } catch (Exception e) {
            DBHandler.getInst().log("FAIL: " + e.toString());
        }
    }

    public static void restartApplication() throws Exception {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(TheGrid.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        //if(!currentJar.getName().endsWith(".jar"))
        //    return;

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());
        command.add ("dbdir:C:\\Databases\\");

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
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

    @Override
    public void dispose() {
        System.out.println("window dispose");
        DBHandler.getInst().close();
        super.dispose();
    }
}


