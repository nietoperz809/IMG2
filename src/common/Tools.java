package common;

import database.DBHandler;
import dialogs.TagListCellRenderer;
import dialogs.TimedMsg2;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import thegrid.ImageViewController;
import thegrid.TheGrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tools {

    private static final ExecutorService globalExecutor = Executors.newCachedThreadPool(); //Executors.newFixedThreadPool(20);

    public static Thread loomThread(Runnable r) {
        return Thread.ofVirtual().start(r);
    }

    public static FutureTask<?> runTask(Runnable r) {
        //System.out.println(ft);
        return (FutureTask<?>) globalExecutor.submit(r);
    }

    /**
     * mark selected items of JList
     * @param searchField contains items to mark
     * @param list1 target JList
     */
    public static void implementSearchboxAction(final JTextField searchField, final JList<String> list1) {
        TagListCellRenderer tlcr = (TagListCellRenderer) list1.getCellRenderer();
        tlcr.removeAll(); // clear all
        String ss = searchField.getText().toLowerCase();
        if (ss.isEmpty())
            list1.repaint(); // all clear?
        else {
            ListModel<String> lm = list1.getModel();
            for (int n = 0; n < lm.getSize(); n++) {
                tlcr.setMark(n, lm.getElementAt(n).contains(ss));
                list1.repaint();
            }
        }
    }

    /**
     * add new Menu Item
     * @param jm JMenu or JPopupMenu
     * @param txt Menu tet
     * @param ali ActionListener
     */
    public static void addMenuItem(JComponent jm, String txt, ActionListener ali) {
        JMenuItem mi1 = new JMenuItem(txt);
        mi1.addActionListener(ali);
        jm.add(mi1);
    }

    /**
     * Checks if a filename has one of n extensions
     *
     * @param fileName the filename
     * @param ext      list of extensions
     * @return true on any match, otherwise false
     */
    public static boolean hasExtension(String fileName, String... ext) {
        fileName = fileName.toLowerCase();
        for (String s : ext) {
            s = s.toLowerCase();
            if (fileName.endsWith(s))
                return true;
        }
        return false;
    }

    public static Color getComplementaryColor(Color color) {
        int R = 255 - color.getRed();
        int G = 255 - color.getGreen();
        int B = 255 - color.getBlue();
        int A = color.getAlpha();
        return new Color(R, G, B, A);
    }

    public static boolean isGIF(String filename) {
        return hasExtension(filename, ".gif");
    }
    public static boolean isMp3(String filename) {
        return hasExtension(filename, ".mp3");
    }

    public static boolean isWEBP(String filename) {
        return hasExtension(filename, ".webp");
    }

    /**
     * Fast scroll of scrollbar content by up/down keys
     *
     * @param keyCode     vvk_up or vk_down
     * @param vp          Viewport of scrollbar
     * @param usePageKeys wether to process pageup/pagedown keys
     */
    public static void fastScroll(int keyCode, JViewport vp, boolean usePageKeys) {
        Point p = vp.getViewPosition();
        switch (keyCode) {
            case KeyEvent.VK_DOWN -> p.y += 10;
            case KeyEvent.VK_PAGE_DOWN -> {
                if (usePageKeys)
                    p.y += 100;
            }
            case KeyEvent.VK_RIGHT -> p.x += 10;
            case KeyEvent.VK_UP -> {
                p.y -= 10;
                if (p.y < 0)
                    return;
            }
            case KeyEvent.VK_PAGE_UP -> {
                if (usePageKeys) {
                    p.y -= 100;
                    if (p.y < 0)
                        return;
                }
            }
            case KeyEvent.VK_LEFT -> {
                p.x -= 10;
                if (p.x < 0)
                    return;
            }
            default -> {
                return;
            }
        }
        vp.setViewPosition(p);
    }

    public static void gc_now() {
        System.gc();
        Runtime.getRuntime().gc();
        //Runtime.getRuntime().runFinalization();
    }

    public static ZipParameters getStandardZipParams() {
        ZipParameters params = new ZipParameters();
        params.setEncryptFiles(true);
        params.setCompressionLevel(CompressionLevel.NORMAL);
        params.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        return params;
    }

    static public byte[] extractResource(String name) throws Exception {
        InputStream is = ClassLoader.getSystemResourceAsStream(name);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int r = Objects.requireNonNull(is).read(buffer);
            if (r == -1) {
                break;
            }
            out.write(buffer, 0, r);
        }

        return out.toByteArray();
    }

    public static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ioe) {
            System.out.println("delay interrupted");
        }
    }

    public static void shutdown(Window gr) {
        boolean[] ret = TimedMsg2.doTimedBox();
        if (ret[0]) // true if cancelled
            return;
        ImageViewController.killAllViews();
        DBHandler.log("--- TheGrid ended");
        gr.setVisible(false);
        DBHandler.closeDatabase(ret[1]);
        System.exit(1);
    }

    public static void restartApplication() throws Exception {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(TheGrid.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());
        command.add("dbdir:" + DBHandler.getDBRoot());
        command.add("nopwd");

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }

    public static String buildQueryForGrid(Set<Integer> foundSet) {
        StringBuilder sqlFound = new StringBuilder();
        sqlFound.append("select name,_ROWID_,tag,accnum from IMAGES where ");
        for (int i : foundSet) {
            sqlFound.append("_rowid_=").append(i).append(" or ");
        }
        sqlFound.setLength(sqlFound.length() - 4);
        return sqlFound.toString();
    }

    public static void newGridForSet(Set<Integer> iset) {
        if (!iset.isEmpty()) {
            String q = buildQueryForGrid(iset);
            (new Thread(() -> new TheGrid(q, "WORKER"))).start();
        }
    }

    public static JToolTip createCustomToolTip(JComponent jc) {
        JToolTip tooltip = new JToolTip();
        tooltip.setComponent(jc);
        tooltip.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        tooltip.setBackground(Color.BLACK);
        tooltip.setForeground(Color.YELLOW);
        return tooltip;
    }

    public static Set<String> listFiles(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    public static void createMisssingDirs(Path p) {
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String commatize(long in) {
        DecimalFormat df = new DecimalFormat("#,###"); // Pattern for thousands separators
        return df.format(in);
    }

// --Commented out by Inspection START (11/10/2025 3:57 PM):
//    /**
//     * Git rev count as build number
//     * @return number of revisions from GIT
//     */
//    public static String getGITrevcount() {
//        try {
//            Process process = Runtime.getRuntime().exec(new String[] {"git", "rev-list", "HEAD", "--count"});
//            process.waitFor();
//            return new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
// --Commented out by Inspection STOP (11/10/2025 3:57 PM)

}
