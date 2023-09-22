package common;

import com.luciad.imageio.webp.WebPReadParam;
import gifdecoder.AnimatedGIFReader;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.security.MessageDigest;

import static common.Tools.hasExtension;

public class ImgTools {


    private record TransferableImage(Image i) implements Transferable, ClipboardOwner {

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (DataFlavor dataFlavor : flavors) {
                if (flavor.equals(dataFlavor)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {

        }
    }

    public static void copyImage (BufferedImage bi)
    {
        TransferableImage trans = new TransferableImage( bi );
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents( trans, trans );
    }

    public static BufferedImage gammaCorrection(BufferedImage original, float gamma) {

        int alpha, red, green, blue;
        int newPixel;

        float gamma_new = 1f / gamma;
        int[] gamma_LUT = gamma_LUT(gamma_new);

        BufferedImage gamma_cor = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {

                // Get pixels by R, G, B
                alpha = new Color(original.getRGB(i, j)).getAlpha();
                red = new Color(original.getRGB(i, j)).getRed();
                green = new Color(original.getRGB(i, j)).getGreen();
                blue = new Color(original.getRGB(i, j)).getBlue();

                red = gamma_LUT[red];
                green = gamma_LUT[green];
                blue = gamma_LUT[blue];

                // Return back to original format
                newPixel = colorToRGB(alpha, red, green, blue);

                // Write pixels into image
                gamma_cor.setRGB(i, j, newPixel);

            }

        }

        return gamma_cor;

    }

    // Create the gamma correction lookup table
    private static int[] gamma_LUT(float gamma_new) {
        int[] gamma_LUT = new int[256];

        for (int i = 0; i < gamma_LUT.length; i++) {
            gamma_LUT[i] = (int) (255 * (Math.pow((float) i / (float) 255, gamma_new)));
        }

        return gamma_LUT;
    }

    // Convert R, G, B, Alpha to standard 8 bit
    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }

    private BufferedImage rotateImage (BufferedImage src, float angle) {
        AffineTransform tx = new AffineTransform();
        tx.rotate(angle, src.getWidth() / 2.0,src.getHeight() / 2.0);
        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);
        return op.filter(src, null);
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
            FileImageInputStream fis = new FileImageInputStream(new File(name));
            reader.setInput(fis);
            // Decode the image
            BufferedImage buff = reader.read(0, readParam);
            fis.close();
            return buff;
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

    public static BufferedImage removeAlpha (BufferedImage img) {
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        g.dispose();
        return newImage;
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

    public static BufferedImage sharpenImage(BufferedImage img) {
        int kernelWidth = 3;
        int kernelHeight = 3;
        int xOffset = (kernelWidth - 1) / 2;
        int yOffset = (kernelHeight - 1) / 2;
        float[] kern = new float[] {
                0.0f, -1.0f, 0.0f,
                -1.0f, 5.0f, -1.0f,
                0.0f, -1.0f, 0.0f
        };
        Kernel kernel = new Kernel(3, 3, kern);


        BufferedImage newSource = new BufferedImage(
                img.getWidth() + kernelWidth - 1,
                img.getHeight() + kernelHeight - 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = newSource.createGraphics();
        g2.drawImage(img, xOffset, yOffset, null);
        g2.dispose();

        ConvolveOp op = new ConvolveOp (kernel,ConvolveOp.EDGE_NO_OP,null);
        return op.filter(newSource, null);
    }

    public static BufferedImage zoomIn (BufferedImage img, Rectangle r) {
        BufferedImage part = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) part.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0,
                img.getWidth(), img.getHeight(),
                r.x, r.y,
                r.x + r.width, r.y + r.height,
                null);
        g.dispose();
        return part;
    }


}