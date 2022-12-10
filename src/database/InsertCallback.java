package database;

import java.awt.image.BufferedImage;

public interface InsertCallback {
    void justInserted (BufferedImage img, String name) throws Exception;
}
