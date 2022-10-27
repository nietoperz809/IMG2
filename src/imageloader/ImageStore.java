package imageloader;


import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class ImageStore {

    public String getSource() {
        return source;
    }

    String source;

    public ImageStore (String root) {
        source = root;
    }
    public abstract java.util.List<String> getFileNames();
    public abstract BufferedImage loadImage (String filename) throws IOException;
    public abstract void close() throws IOException;
}
