package thegrid;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageScaler {

    public static BufferedImage scaleExact(BufferedImage img, Dimension d) {
        float factor = getFactorMin(img.getWidth(), img.getHeight(), d);

        // create the image
        int w = (int) (img.getWidth() * factor);
        int h = (int) (img.getHeight() * factor);
        BufferedImage scaled = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = (Graphics2D) scaled.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, w, h, null);
        return scaled;
    }

    public static BufferedImage scaleDirect(BufferedImage img, Dimension d) {
        BufferedImage scaled = new BufferedImage(d.width, d.height,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = (Graphics2D) scaled.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, d.width, d.height, null);
        return scaled;
    }

    static float getBinFactor(int width, int height, Dimension dim) {
        float factor = 1;
        float target = getFactorMin(width, height, dim);
        if (target <= 1) {
            while (factor / 2 > target) {
                factor /= 2;
            }
        } else {
            while (factor * 2 < target) {
                factor *= 2;
            }
        }
        return factor;
    }

    static float getFactorMin(int width, int height, Dimension dim) {
        return Math.min(dim.width / (float) width, dim.height / (float) height);
    }

    static float getFactorMax(int width, int height, Dimension dim) {
        return Math.max(dim.width / (float) width, dim.height / (float) height);
    }

}