package common;

import java.awt.*;
import java.awt.image.BufferedImage;

/////////////////////////////////////////////////////////////////////////
public class ImageWarper {
    private final Point mFromPoint;
    private final Point mToPoint;
    private final int[] mFromPixels;
    private final int[] mToPixels;
    private final int mWidth;
    private final int mHeight; // width & height of warp image

    public ImageWarper(BufferedImage img, Point fromPoint, Point toPoint) {
        mFromPoint = fromPoint;
        mToPoint = toPoint;
        mWidth = img.getWidth(null);
        mHeight = img.getHeight(null);
        // get the pixel data from the image
        mFromPixels = new int[mWidth * mHeight];
        mToPixels = new int[mWidth * mHeight];
        img.getRGB(0, 0, mWidth, mHeight, mFromPixels, 0, mWidth);
        System.arraycopy(mFromPixels, 0, mToPixels, 0, mWidth * mHeight);
    }

    private static BufferedImage createImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

    // warp mFromPixels into mToPixels
    public BufferedImage warpPixels() {
        int dx = mToPoint.x - mFromPoint.x;
        int dy = mToPoint.y - mFromPoint.y;
        int dist = (int) Math.sqrt(dx * dx + dy * dy) * 2;
        Rectangle r = new Rectangle();
        Point ne = new Point(0, 0);
        Point nw = new Point(0, 0);
        Point se = new Point(0, 0);
        Point sw = new Point(0, 0);
        // copy mFromPixels to mToPixels, so the non-warped parts will be identical
        if (dist == 0) {
            return null;
        }
        // warp northeast quadrant
        SetRect(r, mFromPoint.x - dist, mFromPoint.y - dist, mFromPoint.x, mFromPoint.y);
        ClipRect(r, mWidth, mHeight);
        SetPt(ne, r.x, r.y);
        SetPt(nw, r.x + r.width, r.y);
        SetPt(se, r.x, r.y + r.height);
        SetPt(sw, mToPoint.x, mToPoint.y);
        WarpRegion(r, nw, ne, sw, se);
        // warp nortwest quadrant
        SetRect(r, mFromPoint.x, mFromPoint.y - dist, mFromPoint.x + dist, mFromPoint.y);
        ClipRect(r, mWidth, mHeight);
        SetPt(ne, r.x, r.y);
        SetPt(nw, r.x + r.width, r.y);
        SetPt(se, mToPoint.x, mToPoint.y);
        SetPt(sw, r.x + r.width, r.y + r.height);
        WarpRegion(r, nw, ne, sw, se);
        // warp southeast quadrant
        SetRect(r, mFromPoint.x - dist, mFromPoint.y, mFromPoint.x, mFromPoint.y + dist);
        ClipRect(r, mWidth, mHeight);
        SetPt(ne, r.x, r.y);
        SetPt(nw, mToPoint.x, mToPoint.y);
        SetPt(se, r.x, r.y + r.height);
        SetPt(sw, r.x + r.width, r.y + r.height);
        WarpRegion(r, nw, ne, sw, se);
        // warp southwest quadrant
        SetRect(r, mFromPoint.x, mFromPoint.y, mFromPoint.x + dist, mFromPoint.y + dist);
        ClipRect(r, mWidth, mHeight);
        SetPt(ne, mToPoint.x, mToPoint.y);
        SetPt(nw, r.x + r.width, r.y);
        SetPt(se, r.x, r.y + r.height);
        SetPt(sw, r.x + r.width, r.y + r.height);
        WarpRegion(r, nw, ne, sw, se);
        return createImageFromArray(mToPixels, mWidth, mHeight);
    }

    // warp a quadrilateral into a rectangle (magic!)
    private void WarpRegion(Rectangle fromRect, Point nw, Point ne, Point sw, Point se) {
        int dx = fromRect.width;
        int dy = fromRect.height;
        double invDX = 1.0 / dx;
        double invDY = 1.0 / dy;
        for (int a = 0; a < dx; a++) {
            double aa = a * invDX;
            double x1 = ne.x + (nw.x - ne.x) * aa;
            double y1 = ne.y + (nw.y - ne.y) * aa;
            double x2 = se.x + (sw.x - se.x) * aa;
            double y2 = se.y + (sw.y - se.y) * aa;
            double xin = x1;
            double yin = y1;
            double dxin = (x2 - x1) * invDY;
            double dyin = (y2 - y1) * invDY;
            int toPixel = fromRect.x + a + fromRect.y * mWidth;
            for (int b = 0; b < dy; b++) {
                if (xin < 0) {
                    xin = 0;
                }
                if (xin >= mWidth) {
                    xin = mWidth - 1;
                }
                if (yin < 0) {
                    yin = 0;
                }
                if (yin >= mHeight) {
                    yin = mHeight - 1;
                }
                int pixelValue = mFromPixels[(int) xin + (int) yin * mWidth];
                mToPixels[toPixel] = pixelValue;
                xin += dxin;
                yin += dyin;
                toPixel += mWidth;
            }
        }
    }

    private void ClipRect(Rectangle r, int w, int h) {
        if (r.x < 0) {
            r.width += r.x;
            r.x = 0;
        }
        if (r.y < 0) {
            r.height += r.y;
            r.y = 0;
        }
        if (r.x + r.width >= w) {
            r.width = w - r.x - 1;
        }
        if (r.y + r.height >= h) {
            r.height = h - r.y - 1;
        }
    }

    // SetRect and SetPt are Mac OS functions. I wrote my own versions here
    // so I didn't have to rewrite too much of the code.
    private void SetRect(Rectangle r, int left, int top, int right, int bottom) {
        r.x = left;
        r.y = top;
        r.width = right - left;
        r.height = bottom - top;
    }

    private void SetPt(Point pt, int x, int y) {
        pt.x = x;
        pt.y = y;
    }

}
