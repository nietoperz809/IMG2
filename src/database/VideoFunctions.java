package database;

import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.lang.System.getProperty;

public class VideoFunctions extends DBHandler {

    public static void addVideoFile(File file) {
        try (InputStream in = new BufferedInputStream(
                Files.newInputStream(file.toPath()))) {

            insertVideoRecord(in, file.getName(), file.length());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addGifFile(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            insertGifRecord(fileContent, file.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addWebPFile(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            insertWEBPRecord(fileContent, file.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertVideoRecord(InputStream vidStream,
                                          String name,
                                          long length) {

        try (PreparedStatement prep = connection.prepareStatement(
                "INSERT INTO VIDEOS (vid, name, blobsize) VALUES (?, ?, ?)")) {

            prep.setBinaryStream(1, vidStream, length);
            prep.setString(2, name);
            prep.setLong(3, length);

            prep.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static void insertGifRecord(byte[] gif, String name) {
        try (PreparedStatement prep = connection.prepareStatement(
                "insert into GiFS (gifdata,name,blobsize) values (?,?,?)")) {
            prep.setBytes(1, gif);
            prep.setString(2, name);
            prep.setString(3, String.valueOf(gif.length));
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertWEBPRecord(byte[] webp, String name) {
        try (PreparedStatement prep = connection.prepareStatement(
                "insert into WEBP (webpdata,name,blobsize) values (?,?,?)")) {
            prep.setBytes(1, webp);
            prep.setString(2, name);
            prep.setString(3, String.valueOf(webp.length));
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertMp3Record(byte[] mp3, String name) {
        try (PreparedStatement prep = connection.prepareStatement(
                "insert into MP3 (song,name,blobsize) values (?,?,?)")) {
            prep.setBytes(1, mp3);
            prep.setString(2, name);
            prep.setInt(3, mp3.length);
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addMP3toDatabase (File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            insertMp3Record(fileContent, file.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * load MP3 as File
     * @param name Name of MP3
     * @param outfile Target path or NULL
     * @return a File which is the MP3
     */
    public static File getMP3FromDatabase(String name, String outfile) {
        if (outfile == null)
            outfile = getDefaultOutfile();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT SONG FROM MP3 WHERE NAME ='" + name + "'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try (InputStream in = rs.getBinaryStream(1);
                         OutputStream out = Files.newOutputStream(Path.of(outfile))) {
                        in.transferTo(out);
                        return new File(outfile);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

/*  EXPERIMENTAL
    public static InputStream loadMP3 (String name) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT SONG FROM MP3 WHERE NAME ='" + name + "'");
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getBinaryStream(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
*/

    public static @NotNull String getDefaultOutfile() {
        return getProperty("java.io.tmpdir") + File.separator + "tempfile-" + "myra.dat";
    }

    public static void mycopy (InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
    }

    public static File getVideoAsFile(NameID nid, Pair videoType, String outfile) {
        if (outfile == null)
            outfile = getDefaultOutfile();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT "+videoType.getValue1()+" FROM "+videoType.getValue0()+" WHERE _ROWID_='" + nid.rowid() + "'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try (InputStream in = rs.getBinaryStream(1);
                         OutputStream out = Files.newOutputStream(Path.of(outfile))) {
                        in.transferTo(out);
                        //mycopy (in, out);
                        return new File(outfile);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static String getVideoBlobLen(NameID nid) {
        return queryBlobLen(nid, "VIDEOS", "VID");
    }

    public static String getGifBlobLen(NameID nid) {
        return queryBlobLen(nid, "GIFS", "GIFDATA");
    }

    public static String getWEBPBlobLen(NameID nid) {
        return queryBlobLen(nid, "WEBP", "WEBPDATA");
    }

    public static String getMP3BlobLen(NameID nid) {
        return queryBlobLen(nid, "MP3", "WEBPDATA");
    }

    public static void changeVideoName(String name, int rowid) {
        changeName("VIDEOS", name, rowid);
    }

    public static void changeGifName(String name, int rowid) {
        changeName("GIFS", name, rowid);
    }

    public static void changeWebpName(String name, int rowid) {
        changeName("WEBP", name, rowid);
    }

    public static List<NameID> getAnimatedFileNames(String dbname) {
        String sql = "select * from (select name,_ROWID_,blobsize from " + dbname + ") order by _ROWID_ desc";
        return getNames(sql);
    }

    public static List<NameID> getVideoFileNames() {
        return getAnimatedFileNames("VIDEOS");
    }

    public static List<NameID> getMp3FileNames() {
        return getAnimatedFileNames("MP3");
    }

    public static List<NameID> getGifFileNames() {
        return getAnimatedFileNames("GIFS");
    }

    public static List<NameID> getWebPFileNames() {
        return getAnimatedFileNames("WEBP");
    }
}
