package imageloader;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public abstract class ImageStore {
    public abstract java.util.List<String> getFileNames();
    public abstract BufferedImage loadImage (String filename) throws IOException;
    public abstract void close() throws IOException;
    public abstract boolean delete (String name);
    public abstract void insert (String name, BufferedImage img) throws IOException;
    public abstract void addImages (File[] files) throws Exception;
}
