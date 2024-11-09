package common;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import dialogs.UnlockDialog;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Tools {

    private static final ExecutorService globalExecutor = Executors.newCachedThreadPool(); //Executors.newFixedThreadPool(20);

    public static Thread loomThread(Runnable r) {
        return Thread.ofVirtual ().start (r);
    }

    public static FutureTask<?> runTask(Runnable r) {
        FutureTask<?> ft = (FutureTask<?>) globalExecutor.submit(r);
        System.out.println(ft.toString());
        return ft;
    }

    public static boolean runningFromJAR()
    {
        URL path = Tools.class.getResource("Tools.class");
        return path.toString().startsWith("jar:");
    }

    /**
     * Only works under MS Windoze
     */
    public static void hideConsoleWindow() {
        if (System.getProperty("os.name").startsWith("Windows")) {

            WinDef.HWND hw = Kernel32.INSTANCE.GetConsoleWindow();
            System.out.println("console: " + hw);
            if (hw != null) {
                User32.INSTANCE.ShowWindow(hw, 0);
            }
            //Kernel32.INSTANCE.FreeConsole(); // Detach from Console
        }
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

    public static String adjustCSVString(String in) {
        TreeSet<String> set = SetFromCSVString(in);
        return CsvStringFromSet(set);
    }

    public static java.util.TreeSet<String> SetFromCSVString(String csv) {
        String[] arr = csv.split(",");
        TreeSet<String> ll = new TreeSet<>();
        for (int n = 0; n < arr.length; n++) {
            arr[n] = arr[n].trim();
            if (arr[n].length() > 1) // ignore single-char strings
                ll.add(arr[n]);
        }
        return ll;
    }

    public static String CsvStringFromSet(TreeSet<String> ll) {
        StringBuilder sb = new StringBuilder();
        for (String s : ll) {
            sb.append(s).append(", ");
        }
        String s2 = sb.toString();
        if (s2.endsWith(", ")) {
            s2 = s2.substring(0, s2.length() - 2);
        }
        return s2;
    }

    public static Color getComplementaryColor(Color color) {
        int R = 255 - color.getRed();
        int G = 255 - color.getGreen();
        int B = 255 - color.getBlue();
        int A = color.getAlpha();
        return new Color(R, G, B, A);
        //return R + (G << 8) + ( B << 16) + ( A << 24);
    }

    public static boolean isGIF(String filename) {
        return hasExtension(filename, ".gif");
    }

    public static boolean isWEBP(String filename) {
        return hasExtension(filename, ".webp");
    }

    public static void Error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }


    public static void Info(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Info",
                JOptionPane.PLAIN_MESSAGE);
    }


    public static boolean Question(String msg) {
        Object response = JOptionPane.showInputDialog(null,
                msg,
                "Please select", JOptionPane.QUESTION_MESSAGE,
                null, new String[]{"No", "Yes"}, "No");
        if (response == null)
            return false;
        return response.equals("Yes");
    }

    public static String getInput(String msg) {
        Object response = JOptionPane.showInputDialog(null,
                msg,
                "Please select", JOptionPane.QUESTION_MESSAGE,
                null, null, "");
        return (String) response;
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


    public static String chooseDir(Component parent) {
        JFileChooser f = new JFileChooser();
        PersistString ps = new PersistString("Common.lastDirectory", System.getProperty("user.home"));
        String lastDirectory = ps.get();
        f.setSelectedFile(new File(lastDirectory));
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (f.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String dir = f.getSelectedFile().getAbsolutePath();
            return ps.set(dir);
        }
        return null;
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

    public static void playWave(byte[] data) throws Exception {
        final Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
        InputStream inp = new BufferedInputStream(new ByteArrayInputStream(data));
        clip.open(AudioSystem.getAudioInputStream(inp));
        clip.start();
    }

    public static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ioe) {
            System.out.println("intExc");
        }
    }

    public static void AskforPWD() throws Exception {
        byte[] bt = UnlockDialog.xmain("PWD?").getBytes(Charset.defaultCharset());
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] theMD5digest = md.digest(bt);
        byte[] shouldBe = {46, -123, 13, -68, 98, 92, -32, -104, -55, 20, 57, 79, -73, 120, 116, 50};
        if (!Arrays.equals(theMD5digest,shouldBe)) {
            Sam.speak(".Access denied!");
            Thread.sleep(3000);
            System.exit(0);
        }
    }

}
