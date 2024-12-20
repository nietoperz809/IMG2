package video;

import java.awt.image.BufferedImage;

/**
 * Gemeric decoder interface
 */
public interface AnimDecoder {
    /**
     * Read one single frame
     * @param n frame index
     * @return the frame image
     */
    BufferedImage getFrame(int n);

    /**
     * Get number of frames of this animation
     * @return the frame count
     */
    int getFrameCount();

    /**
     * Must be called first to load the animation
     * @param filename path/file name of the animation
     */
    void read (String filename);
}
