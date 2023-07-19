package common;

import com.luciad.imageio.webp.WebPReadParam;
import gifdecoder.AnimatedGIFReader;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
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
import java.security.MessageDigest;

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

    public static String[] getImageExtensions() {
        return new String[] {"jpg", "jpeg", "png", "bmp", "gif", "jfif", "webp"};
    }


    public static boolean isImage(String in) {
        return hasExtension(in, getImageExtensions());
    }


    public static BufferedImage loadImage(String name) throws IOException {
        if (hasExtension(name, ".gif")) {
            FileInputStream fin = new FileInputStream(name);
            AnimatedGIFReader reader = new AnimatedGIFReader();
            BufferedImage img = reader.read(fin);
            fin.close();
            return img;
        } else if (hasExtension(name, ".webp")) {
            // Obtain a WebP ImageReader instance
            ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
            // Configure decoding parameters
            WebPReadParam readParam = new WebPReadParam();
            readParam.setBypassFiltering(true);
            // Configure the input on the ImageReader
            reader.setInput(new FileImageInputStream(new File(name)));
            // Decode the image
            return reader.read(0, readParam);
        } else {
            return ImageIO.read(new File (name));
        }
    }

    /**
     * Makes BufferedImage vom java.awt.Image
     * @param img primitive image
     * @return buffered image with same content
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    /**
     * Rotate clockwise by 90 degrees
     * @param src source image
     * @return rotated image
     */
    public static BufferedImage rotateClockwise90(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dest = new BufferedImage(height, width,
                src.getType());
        Graphics2D graphics2D = dest.createGraphics();
        graphics2D.translate((height - width) / 2, (height - width) / 2);
        graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
        graphics2D.drawRenderedImage(src, null);
        return dest;
    }

    private BufferedImage rotateImage (BufferedImage src, float angle) {
        AffineTransform tx = new AffineTransform();
        tx.rotate(angle, src.getWidth() / 2.0,src.getHeight() / 2.0);
        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);
        return op.filter(src, null);
    }


    /**
     * Flips image like a mirror
     * @param img original image
     * @return flipped image
     */
    public static BufferedImage flip (BufferedImage img) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        img = op.filter(img, null);
        return img;
    }

    public static byte[] imgHash (BufferedImage in) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(in, "jpg", outputStream);
        byte[] data = outputStream.toByteArray();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        return md.digest();
    }


    /**
     * Converts Image to byte array
     * @param img source image
     * @return the image as byte array
     * @throws IOException if smth. gone wrong
     */
    public static byte[] imgToByteArray (BufferedImage img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write (img, "jpg", baos);
        return baos.toByteArray();
    }

    /**
     * make image from byte array
     * @param arr image als byte array
     * @return a BufferedImage object
     * @throws IOException if smth. gone wrong
     */
    public static BufferedImage byteArrayToImg (byte[] arr) throws IOException {
        InputStream is = new ByteArrayInputStream(arr);
        return ImageIO.read(is);
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
     * @param keyCode vvk_up or vk_down
     * @param vp Viewport of scrollbar
     */
    public static void fastScroll(int keyCode, JViewport vp) {
        Point p = vp.getViewPosition();
        switch (keyCode) {
            case KeyEvent.VK_DOWN -> p.y += 10;
            case KeyEvent.VK_RIGHT -> p.x += 10;
            case KeyEvent.VK_UP -> {
                p.y -= 10;
                if (p.y < 0)
                    return;
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


//    public static void main(String[] args) {
//        chooseDir(null);
//    }
}
