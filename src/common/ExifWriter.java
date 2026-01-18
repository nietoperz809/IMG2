package common;

import org.apache.commons.imaging.*;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.*;
import org.apache.commons.imaging.formats.tiff.*;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.*;

public class ExifWriter {

    public static void setImageDescription(File inputJ, File outputJ, String desc) throws Exception {
        TiffOutputSet outputSet;

        // vorhandene EXIF-Daten lesen (falls vorhanden)
        ImageMetadata metadata = Imaging.getMetadata(inputJ);
        if (metadata instanceof JpegImageMetadata jpegMetadata) {
            TiffImageMetadata exif = jpegMetadata.getExif();
            if (exif != null) {
                outputSet = exif.getOutputSet();
            } else {
                outputSet = new TiffOutputSet();
            }
        } else {
            outputSet = new TiffOutputSet();
        }

        // EXIF Root Directory
        TiffOutputDirectory exifDir =
                outputSet.getOrCreateRootDirectory();

        // vorhandenen Tag entfernen (wichtig!)
        exifDir.removeField(
                TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION
        );

        // neuen Wert setzen
        exifDir.add(
                TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION,
                desc
        );

        // Schreiben
        try (OutputStream os = new BufferedOutputStream(
                new FileOutputStream(outputJ))) {

            new ExifRewriter().updateExifMetadataLossless(
                    inputJ, os, outputSet
            );
        }
    }
}
