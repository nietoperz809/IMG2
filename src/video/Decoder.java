package video;

import java.awt.image.BufferedImage;

public interface Decoder {
    BufferedImage getFrame(int n);
    int getFrameCount();
    void read (String filename);
}
