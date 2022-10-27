package thegrid;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class MemoryFile {
    public String fileName;
    public byte[] contents;

    public MemoryFile(String name, BufferedImage img) {
        fileName = name;
        try {
            contents = Tools.imgToByteArray(img);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
