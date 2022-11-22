package imageloader;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface InsertCallback {
    void newImage (BufferedImage img, String name) throws Exception;
}
