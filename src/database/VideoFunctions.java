package database;

import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class VideoFunctions extends DBHandler{
    public static void addVideoFile(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            insertVideoRecord(fileContent, file.getName());
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

    private static void insertVideoRecord(byte[] vid, String name) {
        PreparedStatement prep;
        try {
            prep = connection.prepareStatement(
                    "insert into VIDEOS (vid,name,blobsize) values (?,?,?)");
            prep.setBytes(1, vid);
            prep.setString(2, name);
            prep.setString(3, String.valueOf(vid.length));
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertGifRecord(byte[] gif, String name) {
        PreparedStatement prep;
        try {
            prep = connection.prepareStatement(
                    "insert into GiFS (gifdata,name,blobsize) values (?,?,?)");
            prep.setBytes(1, gif);
            prep.setString(2, name);
            prep.setString(3, String.valueOf(gif.length));
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertWEBPRecord(byte[] webp, String name) {
        PreparedStatement prep;
        try {
            prep = connection.prepareStatement(
                    "insert into WEBP (webpdata,name,blobsize) values (?,?,?)");
            prep.setBytes(1, webp);
            prep.setString(2, name);
            prep.setString(3, String.valueOf(webp.length));
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static SoftReference<byte[]> loadVideoBytes(NameID nid) throws Exception {
        return loadBytes("select VID from VIDEOS where _ROWID_='" + nid.rowid() + "'");
    }

    public static SoftReference<byte[]> loadGifBytes(NameID nid) throws Exception {
        return loadBytes("select GIFDATA from GIFS where _ROWID_='" + nid.rowid() + "'");
    }

    public static SoftReference<byte[]> loadWEBPBytes(NameID nid) throws Exception {
        return loadBytes("select WEBPDATA from WEBP where _ROWID_='" + nid.rowid() + "'");
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

    public static File transferGifIntoFile(NameID nid) throws Exception {
        return transferIntoFile(nid, "GIF");
    }

    public static File transferwEBPIntoFile(NameID nid) throws Exception {
        return transferIntoFile(nid, "WEBP");
    }

    public static File transferVideoIntoFile(NameID nid) throws Exception {
        return transferIntoFile(nid, "VID");
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

    public static List<NameID> getGifFileNames() {
        return getAnimatedFileNames("GIFS");
    }

    public static List<NameID> getWebPFileNames() {
        return getAnimatedFileNames("WEBP");
    }
}
