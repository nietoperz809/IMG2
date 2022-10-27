/*
package imageloader;

import thegrid.Tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RegularDisk extends ImageStore {

    public RegularDisk(String root) {
        super(root);
    }

    @Override
    public List<String> getFileNames() {
        File dir = new File(source);
        File[] imgFiles = dir.listFiles((name) -> Tools.isImage(name.getName()));
        assert imgFiles != null;
        Arrays.sort(imgFiles, Comparator.comparing(File::lastModified));
        java.util.List<String> names = new ArrayList<>();
        for (File f : imgFiles) names.add(f.getAbsolutePath());
        return names;
    }

    @Override
    public BufferedImage loadImage(String filename) throws IOException {
        return Tools.loadImage (filename);
    }

    @Override
    public void close() throws IOException {
        
    }
}
*/
