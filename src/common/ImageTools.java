package common;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Tools.ImageStatistics;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.luciad.imageio.webp.WebPReadParam;
import database.DBHandler;
import database.ImageImport;
import org.apache.commons.imaging.Imaging;
import org.jetbrains.annotations.NotNull;
import thegrid.ImageList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.Objects;

import static com.google.common.jimfs.Jimfs.newFileSystem;
import static database.DBHandler.loadThumbnail;


public class ImageTools {

    public static class Heatmap {

        public static int selectColorOrder() {
            Object[] options = {"RGB", "BGR", "GRB"};
            return JOptionPane.showOptionDialog(
                    null,
                    "Please select ...",
                    "Heatmap colorization",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2] // Default option
            );
        }

        public static void applyInPlace(FastBitmap fastBitmap) {
            if (fastBitmap.isRGB()) {
                fastBitmap.toGrayscale();
            }

            //(new Invert()).applyInPlace(fastBitmap);

            int size = fastBitmap.getWidth() * fastBitmap.getHeight();
            int min = ImageStatistics.Minimum(fastBitmap);
            int max = ImageStatistics.Maximum(fastBitmap);
            fastBitmap.toRGB();

            int cord = selectColorOrder();

            for (int i = 0; i < size; ++i) {
                int[] rgb = GrayscaleToHeatMap (cord, fastBitmap.getRed(i), min, max);
                fastBitmap.setRGB(i, rgb);
            }
        }

        private static int[] GrayscaleToHeatMap (int order, double gray, double min, double max) {
            int r = 0;
            int g = 0;
            int b = 0;
            gray = (gray - min) / (max - min);
            if (gray <= 0.2) {
                b = (int) (gray / 0.2 * (double) 255.0F);
            } else if (gray > 0.2 && gray <= 0.7) {
                b = (int) (((double) 1.0F - (gray - 0.2) / (double) 0.5F) * (double) 255.0F);
            }

            if (gray >= 0.2 && gray <= 0.6) {
                g = (int) ((gray - 0.2) / 0.4 * (double) 255.0F);
            } else if (gray > 0.6 && gray <= 0.9) {
                g = (int) (((double) 1.0F - (gray - 0.6) / 0.3) * (double) 255.0F);
            }

            if (gray >= (double) 0.5F) {
                r = (int) ((gray - (double) 0.5F) / (double) 0.5F * (double) 255.0F);
            }

            return switch (order) {
                case 0 -> new int[]{r, g, b};
                case 1 -> new int[]{b, g, r};
                case 2 -> new int[]{g, r, b};
                default -> throw new RuntimeException("please choose color order");
            };
        }
    }

    /**
     Make Preview from first dim*dim tiles of a Grid
     @param list ImageList of all imgs
     @param rowcol  rows and columns of new big image
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
                            x, y, 100, 100, null);
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
     * @param anum arbitrary ID (rowid in database)
     * @param outPath path were the Img goes
     */
    public static String saveImg2Disk(BufferedImage img, int anum, String outPath, String name) {
        outPath += File.separator;
        outPath += Objects.requireNonNullElseGet(name, () -> RandomWord.generateWord(-1));
        outPath += "(" + anum + ").jpg";

        //var fs = newFileSystem(Configuration.unix());

        try {
            boolean success = ImageIO.write(img, "jpg", new File(outPath));
            if (!success )
                throw new Exception ("imgIO write fail");
            // Insert EXIF
            String infoTxt = DBHandler.getImageInfo(anum);
            if (infoTxt != null || !infoTxt.isEmpty()) {
                String epath = outPath.replace(".jpg", "exif.jpg");
                ExifWriter.setImageDescription(new File(outPath), new File(epath), infoTxt);
                DeferredFileDeleter.put(outPath);
            }
        } catch (Exception ex) {
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

    public static String writeToFile(Image im2, String format, String dir, String name) {
        try {
            String path = dir + File.separator + name + "." + format;
            ImageIO.write(ImageTools.toBufferedImage(im2), format,
                    new File(path));
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void imageToClipboard(Image bi) {
        TransferableImage trans = new TransferableImage(bi);
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(trans, trans);
        Sam.speak("Image posted to clipboard");
    }

    public static String[] getImageExtensions() {
        return new String[]{"jpg", "jpeg", "png", "bmp", "gif", "jfif", "webp"};
    }

    public static boolean isWEBP(String path) {
        try {
            InputStream in = new FileInputStream(path);
            byte[] header = new byte[12];
            if (in.read(header) == header.length) {
                if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F' &&
                        header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
                    in.close();
                    return true;
                }
            }
        } catch (Exception _) {
        }
        return false;
    }

    public static BufferedImage loadWEBP(String name) throws Exception {
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
    }

    /**
     * Import images from file
     * @param name path to image
     * @return a valid ImageImport or null on error
     */
    public static ImageImport importImageFromFile(String name) {
        try {
            if (isWEBP(name)) {
                return new ImageImport(ImageImport.Decoder.WEBPREADER, loadWEBP(name));
            }
            return new ImageImport(ImageImport.Decoder.IMAGING, Imaging.getBufferedImage(new File(name)));
        } catch (Exception _) {
            try {
                return new ImageImport(ImageImport.Decoder.AWTHACK, readJPGwithAWT(name));
            } catch (Exception _) {
                System.out.println("image decoding fail");
                return null;
            }
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
     * @return The image
     * @throws Exception if smth. gone wrong
     */
    public static BufferedImage readJPGwithAWT(String path) throws Exception {
        Image img = Toolkit.getDefaultToolkit().createImage(path);
        MediaTracker mt = new MediaTracker(new Canvas());
        mt.addImage(img, 0);
        mt.waitForID(0);
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int dim = Math.toIntExact((long) width * height);
        if (dim > 100_000_000L) {
            throw new IllegalStateException("image too big");
        }
        int[] pixels = new int[dim];
        new PixelGrabber(img, 0, 0, width, height, pixels, 0, width).grabPixels();
        DirectColorModel cm = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
        SampleModel sampleModel = cm.createCompatibleWritableRaster(1, 1).
                getSampleModel().createCompatibleSampleModel(width, height);
        DataBuffer dataBuffer = new DataBufferInt(pixels, dim, 0);
        WritableRaster rgbRaster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
        return new BufferedImage(cm, rgbRaster, false, null);
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