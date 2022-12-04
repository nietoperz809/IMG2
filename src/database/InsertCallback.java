package database;

import java.awt.image.BufferedImage;

public interface InsertCallback {
    void newImage (BufferedImage img, String name) throws Exception;
}
