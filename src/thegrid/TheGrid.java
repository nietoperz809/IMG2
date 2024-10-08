package thegrid;

import buildinfo.BuildInfo;
import common.PersistString;
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
import java.util.ArrayList;

import static common.ImgTools.byteArrayToImg;
import static common.Tools.extractResource;
import static java.util.Objects.*;


public class TheGrid extends MyFrame {
    private static int instCount = 0;
    public int thisInstCount;
    public static final PersistString mainSQL =
            new PersistString("mainSQL",
                    "select name,_ROWID_,tag,accnum from IMAGES order by accnum desc");
    public final ImageList imageL = new ImageList();
    //public static TheGrid instance;
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


    public TheGrid (String sql, String dbRoot) {
        instCount++;
        thisInstCount = instCount;
        setTitle(dbRoot);
        imageL.setSQL(sql);
        System.out.println("TheGrid constructor called");
        DBHandler.getInst().log("Images in DB: "+this.imageL.size());
        progress = new ProgressBox(this, this.imageL.size());
        rootPane = new JPanel();
        scrollPane = new JScrollPane(rootPane);
        rootPane.setLayout(new GridLayout(0, 8, 1, 1));
        add(scrollPane);
        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        rootPane.setToolTipText(imageL.size() + " Images, press 'a' to add more");
        new GridListeners(this);
        new GridMenu(this);
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
        String dbRoot = null;
        try {
            if (input.length != 0) {
                if (input[0].startsWith("dbdir:")) {
                    dbRoot = input[0].substring(6);
                    DBHandler.setDBRoot(dbRoot);
                    System.out.println(dbRoot);
                }
            }
//            Thread hook = new Thread(() ->
//                    DBHandler.getInst().log("SHUTDOWN"));
//            Runtime.getRuntime().addShutdownHook(hook);


            DBHandler.getInst().log("+++ TheGrid started");
            new TheGrid (mainSQL.get(), dbRoot);
//            Thread.sleep(100000);
            System.out.println("end main");
        } catch (Exception e) {
            System.out.println("FAIL: " + e);
            DBHandler.getInst().log("FAIL: " + e);
        }
    }

    public static void restartApplication() throws Exception {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(TheGrid.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<>();
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
            GridImage lab = new GridImage(this, thumbnailImage, rootPane, name);
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
        if (this.thisInstCount == 1)
            setTitle (getTitle()+ " " + BuildInfo.buildInfo + " -- " + info);
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
        DBHandler.ThumbHash tbh = null;
        try {
            tbh = DBHandler.getInst().loadThumbnail(rowid);
        } catch (Exception e) {
            System.err.println("thumb read fail: " + rowid);
//            int id = imageL.get(s).rowid();
//            DBHandler.getInst().createNewThumb(id);
//            tbh = DBHandler.getInst().loadThumbnail(rowid);
        }
        GridImage lab = new GridImage(this, tbh, s, rootPane);

        rootPane.add(lab);
        Instant end = Instant.now();
        String info = "Loaded " + (++imageCount) + " Thumbs in " + Duration.between(startTime, end).toMillis() / 1000 + " Seconds";
        progress.setTextAndValue(info, imageCount);
        if (imageCount >= imageL.size()) {
            stopThumbViewFill(info);
        }
    }
}


