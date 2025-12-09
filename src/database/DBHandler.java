package database;

import common.*;
import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import dialogs.UnlockDialog;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Future;

import static common.ImageTools.JPGByteArrayToImg;
import static common.Tools.extractResource;
import static java.lang.System.*;

public class DBHandler {
    static final String NO_PASS = "NoPass";
    public static final Dimension thumbDimension = new Dimension(100,100);
    public static final String DB_FILE = "mydb";
    public static final String DB_EXT = ".mv.db";
    static String RootDirectory =
            "C:\\Databases_Copy\\Databases\\";
    //"E:\\Databases\\";
    static Connection connection;
    static Statement stm;
    /*
        jdbc:h2:C:\peter.home\java\IMG2\datastore\mydb;CIPHER=AES
     */
    static Future<?> transferTask;

//    public static Connection getConnection() {
//        return connection;
//    }

    public static void startDatabase(String root) {
        RootDirectory = root;
        PersistString pers = new PersistString("pwddb", NO_PASS);
        try {
            String aes_pwd;
            if (pers.get().equals(NO_PASS)) {
                aes_pwd = UnlockDialog.xmain(null);
                pers.set(aes_pwd);
            } else {
                aes_pwd = pers.get();
            }
            String url = "jdbc:h2:" + RootDirectory + DB_FILE + ";CIPHER=AES";
            String user = "LALA";
            String pwd = aes_pwd + " dumm";
            out.println("-------------------------");
            out.println(url);
            out.println(user + " -- " + pwd);
            out.println("-------------------------");
            connection = DriverManager.getConnection(url, user, pwd);
            stm = connection.createStatement();
            stm.execute("alter table VIDEOS add if not exists BLOBSiZE INT");
            stm.execute("alter table GIFS add if not exists BLOBSiZE INT");
            stm.execute("alter table WEBP add if not exists BLOBSiZE INT");
            stm.execute("alter table IMAGES add if not exists IMGHASH JAVA_OBJECT");
            stm.execute("create table if not exists QUERIES (sql varbinary(512) primary key)");
            stm.execute("create table if not exists LOG (ltime timestamp GENERATED ALWAYS AS CURRENT_TIMESTAMP, sql varchar(256))");
            stm.execute("create table if not exists VIDEOS (VID blob, NAME varchar(200), HASHVAL blob(16))");
            stm.execute("create table if not exists GIFS (GIFDATA blob, NAME varchar(200), HASHVAL blob(16), TAG varchar(128))");
            stm.execute("create table if not exists WEBP (WEBPDATA blob, NAME varchar(200), HASHVAL blob(16), TAG varchar(128))");
            stm.execute("alter table IMAGES add if not exists TAG varchar(128)");
            stm.execute("alter table IMAGES add if not exists ACCNUM integer");
            stm.execute("alter table VIDEOS add if not exists TAG varchar(128)");
            Sam.speak("deta base is ready!");
        } catch (SQLException e) {
            connection = null;
            Sam.speak("Failed to connect to data base!");
            pers.reset();
            MsgBox.Error(e.toString());
            exit(-1);
        }
    }

    public static String getDBRoot() {
        return RootDirectory;
    }

    /**
     * Warning box if an image is about to be deleted
     *
     * @return true if user clicked OK
     */
    private static boolean askForDel(String imgName) {
        Object[] options = {"OK", "NO! NEVER!!"};
        return JOptionPane.showOptionDialog(null,
                "Delete " + imgName + " from DB?",
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[1]
        ) != 0;
    }

    public static boolean execSQL(String sql) {
        try {
            System.out.println("SQL: "+sql);
            return stm.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * close the DB
     * @param defrag true if defrag allowed
     */
    public static void closeDatabase(boolean defrag) {
        try {
            if (defrag)
                execSQL("shutdown compact");
            else
                execSQL("shutdown immediately");
            connection.close();
            //_inst = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized ResultSet query(String txt) {
        try {
            return stm.executeQuery(txt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String queryString (String sql) {
        try (ResultSet res = query(sql)) {
            if (Objects.requireNonNull(res).next())
                return res.getString(1);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void log(String str) {
        out.println(str);
    }

    public static void reduceLog() {
        try {
            stm.execute("delete from log where _rowid_ < (select max (_rowid_)-50 from log)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDBFileSize() {
        String sql = "SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS where SETTING_NAME = 'info.FILE_SIZE'";
        return queryString(sql);
    }

    public static String getH2Version() {
        String sql = "select H2VERSION()";
        return queryString(sql);
    }

    public static ArrayList<LogMessage> getLog() {
        String sql = "select * from LOG order by ltime";
        ArrayList<LogMessage> al = new ArrayList<>();
        try (ResultSet res = query(sql)) {
            while (Objects.requireNonNull(res).next()) {
                al.add(new LogMessage(res.getString(1),
                        res.getString(2)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return al;
    }

    public static List<NameID> loadImageInfos(String eSQL) {
        return getNames(Objects.requireNonNull(eSQL));
    }

    static synchronized List<NameID> getNames(String sql) {
        ArrayList<NameID> al = new ArrayList<>();
        try (ResultSet res = query(sql)) {
            if (res == null)
                return al;
            while (res.next()) {
                al.add(new NameID(res.getString(1), // name
                        res.getInt(2),              // _rowid_
                        res.getString(3)));         // tag
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return al;
    }

    public static boolean deleteImageSilently(int rowid) {
        try {
            stm.execute("delete from IMAGES where _ROWID_ = " + rowid);
            return true;
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    public static boolean deleteImage(int rowid) {
        if (askForDel(String.valueOf(rowid))) {
            return false;
        }
        return deleteImageSilently(rowid);
    }

    public static void deleteVideo(int rowid) {
        deleteGifOrVideo("VIDEOS", rowid);
    }

    public static void deleteGif(int rowid) {
        deleteGifOrVideo("GIFS", rowid);
    }

    public static void deleteWEBP(int rowid) {
        deleteGifOrVideo("WEBP", rowid);
    }

    public static void deleteGifOrVideo(String tablename, int rowid) {
        if (askForDel(String.valueOf(rowid))) {
            return;
        }
        try {
            stm.execute("delete from " + tablename + " where _ROWID_ = " + rowid);
        } catch (SQLException e) {
            //throw new RuntimeException(e);
        }
    }

    public static void setTag(int rowid, String tag) {
        try {
            stm.execute("update IMAGES set tag = '" + tag + "' where _ROWID_ = " + rowid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTags(int rowid) {
        return queryString("select tag from IMAGES where _ROWID_ = " + rowid);
    }

    public static String getTagsCommaReplaced(int rowid) {
        String tags = getTags(rowid);
        return tags == null ? null : tags.replaceAll(", ", "-");
    }

    public static synchronized TreeSet<String> getImageTagList() {
        TreeSet<String> ll = new TreeSet<>();
        try {
            try (ResultSet res = query("select tag from IMAGES")) {
                while (Objects.requireNonNull(res).next()) {
                    String s = res.getString(1);
                    if (s != null) {
                        TreeSet<String> l2 = Csv.SetFromCSVString(s);
                        ll.addAll(l2);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ll;
    }

    /**
     * Add Files to DB and delete the source
     * @param files Array of files
     * @param ic Callback object after insertion into DB
     * @throws Exception if smth. went wrong
     */
    public static synchronized int MoveImageFilesToDB(File[] files, InsertCallback ic) throws Exception {
        int ret = 0;
        for (File file : files) {
            BufferedImage img = ImageTools.loadImageFromFile(file.getPath());
            if (img == null) {
                err.println("no image");
                continue;
            }
            insertImageRecord(img);
            ic.justInserted(img);
            DeferredFileDeleter.put(file);
            ret++;
        }
        connection.commit();
        return ret;
    }

    private static byte[] createThumbBytes (BufferedImage img) throws IOException {
        BufferedImage thumbnailImage = ImageScaler.scaleExact(img, thumbDimension);
        return ImageTools.imgToJPGByteArray(thumbnailImage);
    }

    /**
     * Convert img into INT_RGB, generate thumbnail and put all in the table
     * @param img the image
     */
    public static void insertImageRecord (BufferedImage img) throws IOException {
        byte[] buff = ImageTools.imgToJPGByteArray(img);
        byte[] buff2 = createThumbBytes(img);
        PreparedStatement prep;
        HashingAlgorithm hasher = new PerceptiveHash(32);
        Hash hash0 = hasher.hash(img);
        try {
            prep = connection.prepareStatement(
                    "insert into IMAGES (image,thumb,imghash) values (?,?,?)");
            prep.setBytes(1, buff);
            prep.setBytes(2, buff2);
            prep.setObject(3, hash0);
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Create Thumbnail 100*100
     */
    public static void createNewThumb(int id) {
        try {
            byte[] bigbytes = loadImage(id);
            BufferedImage bigImg = ImageTools.JPGByteArrayToImg(bigbytes);
            if (bigImg == null) {
                out.println("bigimg load fail: " + id);
                bigImg = JPGByteArrayToImg(extractResource("fail.png"));
            }
            byte[] buff = createThumbBytes(bigImg);
            PreparedStatement prep;
            prep = connection.prepareStatement(
                    "update IMAGES set thumb=? where _rowid_ = " + id);
            prep.setBytes(1, buff);
            prep.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void changeBigImg(BufferedImage img, int id) {
        try {
            //img = ImageTools.removeAlpha(img);
            byte[] buff = ImageTools.imgToJPGByteArray(img);
            HashingAlgorithm hasher = new PerceptiveHash(32);
            Hash hash0 = hasher.hash(img);
            PreparedStatement prep;
            prep = connection.prepareStatement(
                    "update IMAGES set image=?,imghash=? where _rowid_ = " + id);
            prep.setBytes(1, buff);
            prep.setObject(2, hash0);
            prep.execute();
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SoftReference<byte[]> loadBytes(String sql) throws Exception {
        ResultSet res = query(sql);
        if (res == null)
            throw new RuntimeException("no query results");
        if (res.next()) {
            SoftReference<byte[]> bt = new SoftReference<>(res.getBytes(1));
            res.close();
            return bt;
        }
        throw new RuntimeException("no query results");
    }

    public static String queryImageLen(int rowid) {
        return queryString("select LENGTH(IMAGE) from IMAGES where _ROWID_='" + rowid + "'");
    }

    public static String queryBlobLen(NameID nid, String table, String blobentry) {
        String s = queryString("select BLOBSIZE from " + table + " where _ROWID_='" + nid.rowid + "'");
        if (s == null) {
            s = queryString("select LENGTH(" + blobentry + ") from " + table + " where _ROWID_='" + nid.rowid + "'");
            execSQL("update " + table + " set BLOBSIZE=" + s + " where _ROWID_='" + nid.rowid + "'");
        }
        return s;
    }

    public static void cancelFileTransfer() {
        transferTask.cancel(true);
    }

    public static void changeName(String table, String name, int rowid) {
        String sql = "update " + table + " set name ='" + name + "' where _rowid_ =" + rowid;
        try {
            stm.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static synchronized byte[] loadThumbnail(int rowid) {
        String q = "select thumb from IMAGES where _rowid_ =" + rowid;
        try (ResultSet res = query(q)) {
            if (Objects.requireNonNull(res).next()) {
                byte[] bt = res.getBytes(1);
                if (bt == null) {
                    createNewThumb(rowid);
                    return loadThumbnail(rowid);
                }
                return bt;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static synchronized ArrayList<HashId> loadPerceptiveImgHashes() {
        String q = "select imghash,_rowid_ from IMAGES where imghash is not null";
        ArrayList<HashId> list = new ArrayList<>();
        try {
            try (ResultSet res = query(q)) {
                while (Objects.requireNonNull(res).next()) {
                    HashId hid = new HashId((Hash) res.getObject(1), res.getInt(2));
                    list.add(hid);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized byte[] loadImage(int rowid) {
        String q = "select image, imghash from IMAGES where _rowid_ =" + rowid;
        try (ResultSet res = query(q)) {
            if (Objects.requireNonNull(res).next()) {
                byte[] img = res.getBytes(1);
                Hash hash = (Hash) res.getObject(2);
                if (hash == null) { // create hash if missing
                    BufferedImage bi = JPGByteArrayToImg(img);
                    HashingAlgorithm hasher = new PerceptiveHash(32);
                    hash = hasher.hash(bi);
                    PreparedStatement prep = connection.prepareStatement(
                            "update IMAGES set imghash=? where _rowid_ = " + rowid);
                    prep.setObject(1, hash);
                    prep.execute();
                }
                return img;
            }
        } catch (SQLException e) {
            //out.println(e);
            throw new RuntimeException(e);
        }
        return null;
    }

    ///////////////////////////////// Friends

    public record NameID(String name, int rowid, String tag) {
        @Override
        public @NotNull String toString() {
            return name + " : (" + rowid + ") ";
        }
    }

    public record GridQuery(String sql, int rowid) {
        static final String DELIM = "--";

        @Override
        public @NotNull String toString() {
            return rowid + DELIM + sql;
        }

        public static GridQuery fromString(String str) {
            String[] parts = str.split(DELIM);
            return new GridQuery(parts[1], Integer.parseInt(parts[0]));
        }
    }

    public record LogMessage(String time, String entry) {
        @Override
        public @NotNull String toString() {
            return time + " : " + entry + "\n";
        }
    }

    public record HashId(Hash hash, int rowID) {
    }
}

