package denoise;


import java.awt.image.BufferedImage;
import java.awt.Color;


/**
 * Denoise Class: Removes noise in the image and hopefully brings out quality
 * Used for breaking a capture.
 * aevans 7/29/2013
 */
public class Denoise {

    private BufferedImage image;

    /**
     * Empty Constructor
     */
    public Denoise() {
        // TODO empty constructor
    }

    /**
     * Constructor with Buffered Image
     * @param img
     */
    public Denoise(BufferedImage img) {
        image = img;
    }

    /**
     * Constructor with Buffered Image and file path
     * @param img
     * @param inpath
     */
//    public Denoise(BufferedImage img, String inpath) {
//        // TODO constructor that takes in image and path --> performs the blur
//        if (img != null) {
//            image.setImage(img);
//            image.setPath(inpath);
//            perform_denoise();
//        }
//    }

    /**
     * Constructor with file path
     * @param inpath
     */
//    public Denoise(String inpath) {
//        // TODO constructor that takes in a path and loads an image --> performs
//        // the blur
//        Pattern p=Pattern.compile("(?mi)(\\.jpg|\\.jpeg|\\.gif|\\.bmp)");
//        Matcher m=p.matcher(inpath);
//
//        if(m.find()){
//            image.setPath(inpath);
//            try {
//                image.setImage(ImageIO.read(new File(image.getPath())));
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//
//    }

    /**
     * Set an image from a file path
     * @param inpath
     */
//    @Override
//    public void setImage(String inpath) {
//        // TODO sets a patha and attempts to create an image
//        Pattern p=Pattern.compile("(?mi)(\\.jpg|\\.jpeg|\\.gif|\\.bmp)");
//        Matcher m=p.matcher(inpath);
//
//        if(m.find()){
//            image.setPath(inpath);
//            try {
//                image.setImage(ImageIO.read(new File(image.getPath())));
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//
//    }

    /**
     * Set an image from a buffered image
     */
    //@Override
    public void setImage(BufferedImage inimg) {
            image = inimg;
    }

    /**
     * Set the image path
     */
//    public void setPath(String inpath){
//        //TODO set the image path
//        image.setPath(inpath);
//    }


    /**
     * Get the Image path
     */
//    @Override
//    public String getPath(){
//        return image.getPath();
//    }

    /**
     * Get a buffered Image
     * @return
     */
    public BufferedImage getBufferedImage() {
        // TODO return the image
        return image;
    }

    /**
     * Return the image object
     */
    public BufferedImage getImage(){
        return image;
    }

    /**
     * Perform the box average denoise
     */
    public void average_denoise() {
            perform_denoise_average();
        }

    /**
     * Perform the laplace based denoise
     */
    public void laplace_denoise() {
        // TODO call for denoising using laplacian filter

        // set the filetype string and if found, render the image
        if (image != null) {
            // if the path points to an image perform denoise
            perform_denoise();

        }
    }

    /**
     * Save an image
     * @param filetype
     */
//    public void save(String filetype) {
//        image.save();
//    }

    /**
     * Perform an average denoise
     */
    private void perform_denoise_average() {

        // TODO perform an average denoise may work for most images
        /*
         * the kernel applied is1/9|1/9|1/91/9|1/9|1/91/9|1/9|1/9
         *
         * this is a blur and a denoise in one so calling it again only causes a
         * blurier image
         */

        BufferedImage proxyimage=image;

        // the new image to be stored as a denoised image
        BufferedImage image2 = new BufferedImage(proxyimage.getWidth(),proxyimage.getHeight(), BufferedImage.TYPE_INT_RGB);

        // the current position properties
        int x = 0;
        int y = 0;

        // a neighbor pixel to add to the map

        // the image width and height properties
        int width = proxyimage.getWidth();
        int height = proxyimage.getHeight();

        // loop through pixels getting neighbors and resetting colors
        for (x = 1; x < width - 1; x++) {
            for (y = 1; y < height - 1; y++) {

                // get the neighbor pixels for the transform
                Color c00 = new Color(proxyimage.getRGB(x - 1, y - 1));
                Color c01 = new Color(proxyimage.getRGB(x - 1, y));
                Color c02 = new Color(proxyimage.getRGB(x - 1, y + 1));
                Color c10 = new Color(proxyimage.getRGB(x, y - 1));
                Color c11 = new Color(proxyimage.getRGB(x, y));
                Color c12 = new Color(proxyimage.getRGB(x, y + 1));
                Color c20 = new Color(proxyimage.getRGB(x + 1, y - 1));
                Color c21 = new Color(proxyimage.getRGB(x + 1, y));
                Color c22 = new Color(proxyimage.getRGB(x + 1, y + 1));

                // apply the kernel for r
                int r = c00.getRed() / 9 + c01.getRed() / 9 + c02.getRed() / 9
                        + c10.getRed() / 9 + c11.getRed() / 9 + c12.getRed()
                        / 9 + c20.getRed() / 9 + c21.getRed() / 9
                        + c22.getRed() / 9;

                // apply the kernel for g
                int g = c00.getGreen() / 9 + c01.getGreen() / 9
                        + c02.getGreen() / 9 + c10.getGreen() / 9
                        + c11.getGreen() / 9 + c12.getGreen() / 9
                        + c20.getGreen() / 9 + c21.getGreen() / 9
                        + c22.getGreen() / 9;

                // apply the transformation for b
                int b = c00.getBlue() / 9 + c01.getBlue() / 9 + c02.getBlue()
                        + c10.getBlue() / 9 + c11.getBlue() / 9 + c12.getBlue()
                        / 9 + c20.getBlue() / 9 + c21.getBlue() / 9
                        + c22.getBlue() / 9;

                // set the new rgb values
                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));

                Color c = new Color(r, g, b);

                image2.setRGB(x, y, c.getRGB());

            }
        }

        // reset the image
        image = image2;
    }

    /**
     * Average out the noise from the denoise filter
     * Take the new buffered image and take averages where the white spots
     * are since these are where the noise is noise is denoted by a pixel whose
     * laplace position is white insead of black/grey
     *
     * @param img2
     */
    private void denoise_compare_from_fft(BufferedImage img2) {
        // TODO take the laplace bitmap and compare the FFT

        BufferedImage proxyimage=image;

        int ri = 0;
        int gi = 0;
        int bi = 0;

        for (int x = 0; x < proxyimage.getWidth(); x++) {
            for (int y = 0; y < proxyimage.getHeight(); y++) {
                // the values from the laplace map
                ri = new Color(img2.getRGB(x, y)).getRed();
                gi = new Color(img2.getRGB(x, y)).getGreen();
                bi = new Color(img2.getRGB(x, y)).getBlue();

                // if hte laplace map is white, this is noise and average are
                // taken
                if (ri == 255 & gi == 255 & bi == 255) {
                    // get the neighbor pixels for the transform
                    Color c00 = new Color(proxyimage.getRGB(x - 1, y - 1));
                    Color c01 = new Color(proxyimage.getRGB(x - 1, y));
                    Color c02 = new Color(proxyimage.getRGB(x - 1, y + 1));
                    Color c10 = new Color(proxyimage.getRGB(x, y - 1));
                    Color c11 = new Color(proxyimage.getRGB(x, y));
                    Color c12 = new Color(proxyimage.getRGB(x, y + 1));
                    Color c20 = new Color(proxyimage.getRGB(x + 1, y - 1));
                    Color c21 = new Color(proxyimage.getRGB(x + 1, y));
                    Color c22 = new Color(proxyimage.getRGB(x + 1, y + 1));

                    // apply the kernel for r
                    int r = c00.getRed() / 9 + c01.getRed() / 9 + c02.getRed()
                            / 9 + c10.getRed() / 9 + c11.getRed() / 9
                            + c12.getRed() / 9 + c20.getRed() / 9
                            + c21.getRed() / 9 + c22.getRed() / 9;

                    // apply the kernel for g
                    int g = c00.getGreen() / 9 + c01.getGreen() / 9
                            + c02.getGreen() / 9 + c10.getGreen() / 9
                            + c11.getGreen() / 9 + c12.getGreen() / 9
                            + c20.getGreen() / 9 + c21.getGreen() / 9
                            + c22.getGreen() / 9;

                    // apply the transformation for b
                    int b = c00.getBlue() / 9 + c01.getBlue() / 9
                            + c02.getBlue() + c10.getBlue() / 9 + c11.getBlue()
                            / 9 + c12.getBlue() / 9 + c20.getBlue() / 9
                            + c21.getBlue() / 9 + c22.getBlue() / 9;

                    // set the new rgb values
                    r = Math.min(255, Math.max(0, r));
                    g = Math.min(255, Math.max(0, g));
                    b = Math.min(255, Math.max(0, b));

                    Color c = new Color(r, g, b);

                    proxyimage.setRGB(x, y, c.getRGB());
                }
            }
        }

    }

    /**
     * Perform a denoise
     */
    private void perform_denoise() {
        // TODO performs the denoise in laplace *default

        BufferedImage proxyimage=image;

        // the new buffered image for the denoising algorithm
        BufferedImage image2 = new BufferedImage(proxyimage.getWidth(),proxyimage.getHeight(), BufferedImage.TYPE_INT_RGB);

        // gives ultimate control can also use image libraries
        // the current position properties
        int x = 0;
        int y = 0;

        // the image width and height properties
        int width = proxyimage.getWidth();
        int height = proxyimage.getHeight();

        /*
         * Denoise Using Rewritten Code found at
         * http://introcs.cs.princeton.edu/
         * java/31datatype/LaplaceFilter.java.html
         *
         * Using laplace is better than averaging the neighbors from each part
         * of an image as it does a better job of getting rid of gaussian noise
         * without overdoing it
         *
         * Applies a default filter:
         *
         * -1|-1|-1 -1|8|-1 -1|-1|-1
         */

        // perform the laplace for each number
        for (y = 1; y < height - 1; y++) {
            for (x = 1; x < width - 1; x++) {

                // get the neighbor pixels for the transform
                Color c00 = new Color(proxyimage.getRGB(x - 1, y - 1));
                Color c01 = new Color(proxyimage.getRGB(x - 1, y));
                Color c02 = new Color(proxyimage.getRGB(x - 1, y + 1));
                Color c10 = new Color(proxyimage.getRGB(x, y - 1));
                Color c11 = new Color(proxyimage.getRGB(x, y));
                Color c12 = new Color(proxyimage.getRGB(x, y + 1));
                Color c20 = new Color(proxyimage.getRGB(x + 1, y - 1));
                Color c21 = new Color(proxyimage.getRGB(x + 1, y));
                Color c22 = new Color(proxyimage.getRGB(x + 1, y + 1));

                /* apply the matrix */
                // to check, try using gauss jordan

                // apply the transformation for r
                int r = -c00.getRed() - c01.getRed() - c02.getRed()
                        + -c10.getRed() + 8 * c11.getRed() - c12.getRed()
                        + -c20.getRed() - c21.getRed() - c22.getRed();

                // apply the transformation for g
                int g = -c00.getGreen() - c01.getGreen() - c02.getGreen()
                        + -c10.getGreen() + 8 * c11.getGreen() - c12.getGreen()
                        + -c20.getGreen() - c21.getGreen() - c22.getGreen();

                // apply the transformation for b
                int b = -c00.getBlue() - c01.getBlue() - c02.getBlue()
                        + -c10.getBlue() + 8 * c11.getBlue() - c12.getBlue()
                        + -c20.getBlue() - c21.getBlue() - c22.getBlue();

                // set the new rgb values
                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));
                Color c = new Color(r, g, b);

                image2.setRGB(x, y, c.getRGB());
            }
        }

        // compare the original image and the image where noise was found and
        // average where noise was found
        denoise_compare_from_fft(image2);
    }

    /**
     * Save the image
     */
//    @Override
//    public void save() {
//        // TODO save the image
//        image.save();
//    }

    /**
     * Delete the image
     */
//    @Override
//    public void delete(){
//        image.delete();
//    }

}


