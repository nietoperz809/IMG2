package video;

import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;

/**
 * Gemeric decoder interface
 */
public interface AnimDecoder {

    BufferedImage stopSymbol = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

    void decodeFile (String filename, BlockingQueue<BufferedImage> outputQue);
}
