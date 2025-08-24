package common;

import database.DBHandler;
import dialogs.TimedMsg2;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import thegrid.ImageViewController;
import thegrid.TheGrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

//    public static boolean isRunningFromJAR()
//    {
//        URL path = Tools.class.getResource("Tools.class");
//        return path.toString().startsWith("jar:");
//    }

//    public static ArrayList<Object> convertObjectToList(Object obj) {
//        ArrayList<Object> list = new ArrayList<>();
//        if (obj.getClass().isArray()) {
//            list = (ArrayList<Object>) Arrays.asList((Object[])obj);
//        } else if (obj instanceof Collection) {
//            list = new ArrayList<>((Collection<?>)obj);
//        }
//        return list;
//    }

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
        boolean b = TimedMsg2.doTimedBox();
        if (b)
            return;
        ImageViewController.killAllViews();
        DBHandler.log("--- TheGrid ended");
        gr.setVisible(false);
        DBHandler.closeDatabase();
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

//    public static void copyUsingFileChannel(Path source, Path destination) throws IOException {
//        try (FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
//             FileChannel destChannel = FileChannel.open(destination,
//                     StandardOpenOption.CREATE,
//                     StandardOpenOption.WRITE)) {
//
//            long transferred = 0;
//            long size = sourceChannel.size();
//            while (transferred < size) {
//                transferred += sourceChannel.transferTo(
//                        transferred,
//                        size - transferred,
//                        destChannel
//                );
//            }
//        }
//    }

    public static void createMisssingDirs (Path p) {
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Git rev count as build number
     * @return number of revisions from GIT
     */
    public static String getGITrevcount() {
        try {
            Process process = Runtime.getRuntime().exec(new String[] {"git", "rev-list", "HEAD", "--count"});
            process.waitFor();
            return new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
