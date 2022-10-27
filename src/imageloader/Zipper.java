package imageloader;

import thegrid.MemoryFile;
import thegrid.Tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper extends ImageStore {

    private final ZipFile zipFile;

    /**
     * Initialize the Zipper
     *
     * @param zpath path of Zipfile
     * @throws IOException if smth. gone wrong
     */
    public Zipper(String zpath) throws IOException {
        super(zpath);
        File fp = new File(zpath);
        if (!fp.exists()) {
            InputStream is = Tools.getResource("empty.zip");
            byte[] array = is.readAllBytes();
            Files.write(Paths.get(zpath), array);
        }
        zipFile = new ZipFile(zpath);
    }

    /**
     * Add files to existing ZIP
     * @param source Physical ZIP file
     * @param memoryFiles list of in-memory files to be added
     */
    public static void addFilesToZip(File source, List<MemoryFile> memoryFiles) {
        try {
            File tmpZip = File.createTempFile(source.getName(), null);
            tmpZip.delete();
            if (!source.renameTo(tmpZip)) {
                throw new Exception("Could not make temp file (" + source.getName() + ")");
            }
            byte[] buffer = new byte[1024];
            ZipInputStream zin = new ZipInputStream(Files.newInputStream(tmpZip.toPath()));
            ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(source.toPath()));

            // Copy new files
            for (MemoryFile memoryFile : memoryFiles) {
                ZipEntry zipEntry = new ZipEntry(memoryFile.fileName);
                out.putNextEntry(zipEntry);
                out.write(memoryFile.contents);
                out.closeEntry();
            }


            // Copy old stuff
            for (ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()) {
                out.putNextEntry(ze);
                for (int read = zin.read(buffer); read > -1; read = zin.read(buffer)) {
                    out.write(buffer, 0, read);
                }
                out.closeEntry();
            }

            out.close();
            tmpZip.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Destruccor, close the Zipfile
     *
     * @throws IOException if smth. gone wrong
     */
    public void close() throws IOException {
        zipFile.close();
    }

    @Override
    public boolean delete(String name) {
        System.out.println("unsupported");
        return false;
    }

    @Override
    public boolean insert(String name, BufferedImage img) {
        System.out.println("unsupported");
        return false;
    }

    /**
     * get list of Zipfile contens
     *
     * @return the list
     */
    public java.util.List<String> getFileNames() {
        ArrayList<String> list = new ArrayList<>();
        zipFile.stream()
                .map(ZipEntry::getName)
                .forEach(list::add);
        return list;
    }

    /**
     * read a single image from the ZIP
     *
     * @param filename name of the image
     * @return the Image itself
     * @throws IOException if smth. gone wrong
     */
    public BufferedImage loadImage(String filename) throws IOException {
        InputStream is = zipFile.getInputStream(zipFile.getEntry(filename));
        BufferedImage img = ImageIO.read(is);
        return img;
    }
}
