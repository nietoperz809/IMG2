package database;

import java.awt.image.BufferedImage;

public record ImageImport(Decoder dec, BufferedImage image) {
    public enum Decoder {AWTHACK, IMAGING, LOCALCOPY, WEBPREADER}
}
