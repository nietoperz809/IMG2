package common;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

import static javax.swing.JOptionPane.YES_NO_OPTION;

public class Tools {
    /**
     * Checks if a filename has one of n extensions
     * @param fileName the filename
     * @param ext list of extensions
     * @return true on any match, otherwise false
     */
    public static boolean hasExtension (String fileName, String... ext) {
        fileName = fileName.toLowerCase();
        for (String s : ext) {
            s = s.toLowerCase();
            if (fileName.endsWith(s))
                return true;
        }
        return false;
    }

    public static void Error (String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }


    public static void Info (String msg) {
        JOptionPane.showMessageDialog(null, msg, "Info",
                JOptionPane.PLAIN_MESSAGE);
    }


    public static boolean Question (String msg) {
        int i = JOptionPane.showConfirmDialog (null, msg,
                "Do it?", YES_NO_OPTION);
        return i == 0; /* true if yes */
    }

    /**
     * Fast scroll of scrollbar content by up/down keys
     *
     * @param keyCode  vvk_up or vk_down
     * @param vp       Viewport of scrollbar
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
        Runtime.getRuntime().runFinalization();
    }

    public static String chooseDir (Component parent) {
        JFileChooser f = new JFileChooser();
        PersistString ps = new PersistString("Common.lastDirectory", System.getProperty("user.home"));
        String lastDirectory = ps.get();
        f.setSelectedFile(new File(lastDirectory));
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (f.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String dir = f.getSelectedFile().getAbsolutePath();
            return ps.set (dir);
        }
        return null;
    }

    static public byte[] extractResource (String name) throws Exception
    {
        InputStream is = ClassLoader.getSystemResourceAsStream (name);

        ByteArrayOutputStream out = new ByteArrayOutputStream ();
        byte[] buffer = new byte[1024];
        while (true)
        {
            int r = is.read (buffer);
            if (r == -1)
            {
                break;
            }
            out.write (buffer, 0, r);
        }

        return out.toByteArray ();
    }

    public static void playWave (byte[] data) throws Exception
    {
        final Clip clip = (Clip) AudioSystem.getLine (new Line.Info (Clip.class));
        InputStream inp  = new BufferedInputStream(new ByteArrayInputStream (data));
        clip.open (AudioSystem.getAudioInputStream (inp));
        clip.start ();
    }

//    public static File readAsFileList(Clipboard clipboard) {
//        try {
//            java.util.List<File> paths = (java.util.List<File>)clipboard.getData(DataFlavor.javaFileListFlavor);
//            return paths.isEmpty() ? null : paths.iterator().next();
//        } catch(Exception e) {
//            System.err.println(e);
//            return null;
//        }
//    }
//
//    public static File readAsString(Clipboard clipboard) {
//        try {
//            return (File)clipboard.getData(DataFlavor.stringFlavor);
//        } catch(Exception e) {
//            System.err.println(e);
//            return null;
//        }
//    }


//    public static void main(String[] args) {
//        chooseDir(null);
//    }
}
