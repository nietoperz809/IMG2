package common;

import com.luciad.imageio.webp.WebPReadParam;
import org.jetbrains.annotations.NotNull;
import thegrid.ImageList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.Objects;

import static common.Tools.hasExtension;
import static database.DBHandler.loadThumbnail;

public class ImageTools {
    /**
     Make Preview from first dim*dim tiles of a Grid
     @param list ImageList
     @param rowcol  rows and columns of new image
     */
    public static BufferedImage createPreviewImage(ImageList list, final int rowcol) {
        final int k = rowcol * 102 + 2;
        BufferedImage big = new BufferedImage(k, k, BufferedImage.TYPE_INT_RGB);
        Graphics2D ig2 = big.createGraphics();
        ig2.setBackground(Color.YELLOW);
        ig2.clearRect(0, 0, k, k);
        int i = 0;
        for (int x = 2; x < k; x += 102) {
            for (int y = 2; y < k; y += 102) {
                try {
                    ig2.drawImage(JPGByteArrayToImg(loadThumbnail(list.get(i++).rowid())),
                            x, y, 100,100,null);
                } catch (RuntimeException e) {
                    //throw new RuntimeException(e);
                }
            }
        }
        return big;
    }

    /**
     * save IMG zo Disk
     * @param img the Image
     * @param anum arbitrary ID
     * @param outPath path were the Img goes
     */
    public static String saveImg2Disk(BufferedImage img, int anum, String outPath, String name) {
        outPath += File.separator;
        outPath += Objects.requireNonNullElseGet(name, () -> RandomWord.generateWord(-1));
        outPath += "(" + anum + ").jpg";
        System.out.println(outPath);
        try {
            boolean success = ImageIO.write(img, "jpg", new File(outPath));
            if (!success)
                System.err.println("imgIO write fail ");
        } catch (Exception ex) {
            System.err.println("imgIO write fail " + ex);
            throw new RuntimeException(ex);
        }
        return outPath;
    }

    private record TransferableImage(Image i) implements Transferable, ClipboardOwner {
        @Override
        public @NotNull Object getTransferData(DataFlavor flavor)
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

    public static void writeToFile(Image im2, String format, String dir, String name) {
        try {
            ImageIO.write(ImageTools.toBufferedImage(im2), format,
                    new File(dir + File.separator + name + "." + format));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void imageToClipboard(Image bi) {
        TransferableImage trans = new TransferableImage(bi);
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(trans, trans);
        Sam.speak ("Image posted to clipboard");
    }

    public static String[] getImageExtensions() {
        return new String[]{"jpg", "jpeg", "png", "bmp", "gif", "jfif", "webp"};
    }

    public static BufferedImage loadImageFromFile(String name) throws IOException {
        if (Tools.isGIF(name)) {
            MsgBox.Info("Please put animated gifs in video app");
            return ImageIO.read(new File(name));
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
            BufferedImage img = BImgFromFile(name);
            if (img == null)
                return ImageIO.read(new File(name));
            return img;
        }
    }

    /**
     * Makes BufferedImage vom java.awt.Image
     * @param img primitive image
     * @return buffered image with same content
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
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

    public static BufferedImage removeAlpha(BufferedImage img) {
        if (img.getType() == BufferedImage.TYPE_INT_RGB)
            return img;
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        g.dispose();
        return newImage;
    }

    public static BufferedImage toGray(BufferedImage img) {
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
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
    public static BufferedImage flip(BufferedImage img) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        img = op.filter(img, null);
        return img;
    }

    /**
     * Converts Image to byte array
     * @param img source image
     * @return the image as byte array
     * @throws IOException if smth. gone wrong
     */
    public static byte[] imgToJPGByteArray(BufferedImage img) throws IOException {
        img = removeAlpha(img);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        return baos.toByteArray();
    }

    /**
     * Loading JPEGs the imageJ style, avoiding color bugs in imageIO
     * @param path path to jpeg
     * @return image or NULL
     */
    public static BufferedImage BImgFromFile (String path) {
        BufferedImage wimg = null;
        try {
            Image img = Toolkit.getDefaultToolkit().createImage(path);
            int width, height;
            do {
                Thread.sleep(10);
                width = img.getWidth(null);
                height = img.getHeight(null);
            } while (width == -1 || height == -1);
            int[] pixels = new int[width * height];
            PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
            pg.grabPixels();
            DirectColorModel cm = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
            WritableRaster wr = cm.createCompatibleWritableRaster(1, 1);
            SampleModel sampleModel = wr.getSampleModel();
            sampleModel = sampleModel.createCompatibleSampleModel(width, height);
            DataBuffer dataBuffer = new DataBufferInt(pixels, width * height, 0);
            WritableRaster rgbRaster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
            wimg = new BufferedImage(cm, rgbRaster, false, null);
        } catch (InterruptedException e) {
            return null;
        }
        return wimg;
    }


    /**
     * make image from byte array
     * Doesnt work for PNG
     * @param arr image als byte array
     * @return a BufferedImage object
     */
    public static BufferedImage JPGByteArrayToImg(byte[] arr) {
        InputStream is = new ByteArrayInputStream(arr);
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage sharpenImage(BufferedImage img, boolean kern) {
        int kernelWidth = 3;
        int kernelHeight = 3;
        int xOffset = (kernelWidth - 1) / 2;
        int yOffset = (kernelHeight - 1) / 2;

        final float[] sharpenMatrix = {
                0.0f, -0.2f, 0.0f,
                -0.2f, 1.8f, -0.2f,
                0.0f, -0.2f, 0.0f
        };

        final float[] kern2 = {
                0.0f, -1.0f, 0.0f,
                -1.0f, 5.0f, -1.0f,
                0.0f, -1.0f, 0.0f
        };

        Kernel kernel = new Kernel(3, 3, kern ? sharpenMatrix : kern2);

        BufferedImage newSource = new BufferedImage(
                img.getWidth() + kernelWidth - 1,
                img.getHeight() + kernelHeight - 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = newSource.createGraphics();
        g2.drawImage(img, xOffset, yOffset, null);
        g2.dispose();

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(newSource, null);
    }

    public static BufferedImage crop(BufferedImage img, Rectangle r) {
        BufferedImage part = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) part.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0,
                r.width, r.height,
                r.x, r.y,
                r.x + r.width, r.y + r.height,
                null);
        g.dispose();
        return part;
    }

    public static BufferedImage deepCopy(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}