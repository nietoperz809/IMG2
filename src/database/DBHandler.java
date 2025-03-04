package database;

import common.*;
import dialogs.UnlockDialog;
import thegrid.ImageScaler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.ref.SoftReference;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;


import static common.ImgTools.byteArrayToImg;
import static common.Tools.extractResource;

public class DBHandler {

    private static final String NO_PASS = "NoPass";
    private static final String DB_FILE = "mydb";
    private static final String DB_FILE_FULL = DB_FILE + ".mv.db";
    private static String ROOT_DIR = "E:\\Databases\\";
    private static Connection connection;
    private static Statement statement;
    /*
        jdbc:h2:C:\peter.home\java\IMG2\datastore\mydb;CIPHER=AES
     */
    private static volatile boolean _backupIsRunning;
    private static Future<?> transferTask;

    static {
        PersistString pers = new PersistString("pwddb", NO_PASS);
        try {
            String aes_pwd;
            if (pers.get().equals(NO_PASS)) {
                aes_pwd = UnlockDialog.xmain(null);
                pers.set(aes_pwd);
            } else {
                aes_pwd = pers.get();
            }
            String url = "jdbc:h2:" + ROOT_DIR + DB_FILE + ";CIPHER=AES";
            String user = "LALA";
            String pwd = aes_pwd + " dumm";
            System.out.println("-------------------------");
            System.out.println(url);
            System.out.println(user + " -- " + pwd);
            System.out.println("-------------------------");
            connection = DriverManager.getConnection(url, user, pwd);
            statement = connection.createStatement();
            String sql;
            sql = "alter table VIDEOS add if not exists BLOBSiZE INT";
            statement.execute(sql);
            sql = "alter table GIFS add if not exists BLOBSiZE INT";
            statement.execute(sql);
            sql = "alter table WEBP add if not exists BLOBSiZE INT";
            statement.execute(sql);
            sql = "alter table IMAGES add if not exists IMGHASH JAVA_OBJECT";
            statement.execute(sql);

            sql = "create table if not exists LOG " +
                    "(ltime timestamp GENERATED ALWAYS AS CURRENT_TIMESTAMP, entry varchar(256))";
            statement.execute(sql);
            // create video table
            sql = "create table if not exists VIDEOS " +
                    "(VID blob, NAME varchar(200), HASHVAL blob(16))";
            statement.execute(sql);
            // create GIF table
            sql = "create table if not exists GIFS " +
                    "(GIFDATA blob, NAME varchar(200), HASHVAL blob(16), TAG varchar(128))";
            statement.execute(sql);
            sql = "create table if not exists WEBP " +
                    "(WEBPDATA blob, NAME varchar(200), HASHVAL blob(16), TAG varchar(128))";
            statement.execute(sql);
//            sql = "alter table IMAGES drop column hashval";
//            statement.execute(sql);
            sql = "alter table IMAGES add if not exists TAG varchar(128)";
            statement.execute(sql);
            sql = "alter table IMAGES add if not exists ACCNUM integer";
            statement.execute(sql);
            sql = "alter table VIDEOS add if not exists TAG varchar(128)";
            statement.execute(sql);
            Sam.speak("deta base is ready!");
        } catch (SQLException e) {
            Sam.speak("Failed to connect to data base!");
            pers.reset();
            Tools.Error(e.toString());
            System.exit(-1);
        }
    }

    public static String getDBRoot() {
        return ROOT_DIR;
    }

    public static void setDBRoot(String s) {
        ROOT_DIR = s;
        log("DBROOT set to:" + s);
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
            return statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try {
            connection.close();
            //_inst = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized ResultSet query(String txt) {
        try {
            return statement.executeQuery(txt);
        } catch (SQLException e) {
            return null; //throw new RuntimeException(e);
        }
    }

    public static void log(String str) {
//        try {
//            statement.execute("insert into LOG(entry) values ('"+str+"')");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    public static void reduceLog() {
        try {
            statement.execute("delete from log where _rowid_ < (select max (_rowid_)-50 from log)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getH2Version() {
        String sql = "select H2VERSION()";
        try (ResultSet res = query(sql)) {
            if (Objects.requireNonNull(res).next())
                return res.getString(1);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    public static List<NameID> getAnimatedFileNames(String dbname) {
        String sql = "select name,_ROWID_,tag from " + dbname + " order by _ROWID_ asc";
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

    private static synchronized List<NameID> getNames(String sql) {
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
            statement.execute("delete from IMAGES where _ROWID_ = " + rowid);
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
            statement.execute("delete from " + tablename + " where _ROWID_ = " + rowid);
        } catch (SQLException e) {
            //throw new RuntimeException(e);
        }
    }

    public static void setTag(int rowid, String tag) {
        try {
            statement.execute("update IMAGES set tag = '" + tag + "' where _ROWID_ = " + rowid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void setTags(int rowid, String... tag) {
//        if (tag.length == 0)
//            return;
//        StringBuilder sb = new StringBuilder();
//        for (int s=0; s<tag.length; s++)
//        {
//            sb.append(tag[s]);
//            if ((tag.length > 1) && (s != tag.length-1))
//                sb.append(',');
//        }
//        setTag (rowid, sb.toString());
//    }

    public static String getTags(int rowid) {
        String strres = null;
        try {
            try (ResultSet res = query("select tag from IMAGES where _ROWID_ = " + rowid)) {
                if (Objects.requireNonNull(res).next()) {
                    strres = res.getString(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return strres;
    }

    public static synchronized TreeSet<String> getImageTagList() {
        TreeSet<String> ll = new TreeSet<>();
        try {
            try (ResultSet res = query("select tag from IMAGES")) {
                while (Objects.requireNonNull(res).next()) {
                    String s = res.getString(1);
                    if (s != null) {
                        TreeSet<String> l2 = Tools.SetFromCSVString(s);
                        ll.addAll(l2);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ll;
    }

    public static void backup() {
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")
                .format(new java.util.Date());
        final String dest = ROOT_DIR + timeStamp + ".backup";
        final String src = ROOT_DIR + DB_FILE_FULL;

        close();

        new Thread(() -> {
            InputStream in = null;
            OutputStream out = null;
            Instant startTime = Instant.now();
            try {
                in = new BufferedInputStream(new FileInputStream(src));
                out = new BufferedOutputStream(new FileOutputStream(dest));
                final byte[] buffer = new byte[1024 * 1024 * 4];
                int lengthRead;
                long total = 0;
                _backupIsRunning = true;
                while ((lengthRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, lengthRead);
                    total += lengthRead;
                    System.out.print("." + total);
                    //Thread.yield();
                    //System.out.print(".");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                _backupIsRunning = false;
                Instant end = Instant.now();
                String msg = "DB backup took: " + Duration.between(startTime, end).toSeconds() + " Seconds";
                Tools.Info(msg);
                System.out.println("done!");
                try {
                    Objects.requireNonNull(out).close();
                    Objects.requireNonNull(in).close();
                } catch (IOException e) {
                    //System.out.println(new RuntimeException(e));
                }
            }
        }).start();
    }

    /**
     * Add Files to DB and delete the source
     * @param files Array of files
     * @param ic Callback object after insertion into DB
     * @throws Exception if smth. went wrong
     */
    public static int MoveImageFilesToDB(File[] files, InsertCallback ic) throws Exception {
        int ret = 0;
        for (File file : files) {
            String name = UUID.randomUUID().toString();
            BufferedImage img = ImgTools.loadImageFromFile(file.getPath());
            if (img == null) {
                System.err.println("no image");
                continue;
            }
            insertImageRecord(name, img);
            ic.justInserted(img, name);
            DeferredFileDeleter.put(file);
            ret++;
        }
        connection.commit();
        return ret;
    }

    /**
     * Convert img into INT_RGB, generate thumbnail and put all int the tabke
     * @param name image name, can be any string
     * @param img th image
     */
    public static void insertImageRecord(String name, BufferedImage img) throws IOException {
        BufferedImage big = ImageScaler.scaleExact(img,
                new Dimension(img.getWidth(), img.getWidth()));
        byte[] buff = ImgTools.imgToByteArray(big);
        BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                new Dimension(100, 100));
        byte[] buff2 = ImgTools.imgToByteArray(thumbnailImage);
        stat_insertImageRecord(buff, buff2, name);
    }

    public static void createNewThumb(int id) {
        try {
            byte[] bigbytes = loadImage(id);
            BufferedImage bigImg = ImgTools.byteArrayToImg(bigbytes);
            if (bigImg == null) {
                System.out.println("bigimg load fail: " + id);
                bigImg = byteArrayToImg(extractResource("fail.png"));
            }
            BufferedImage thumbnailImage = ImageScaler.scaleExact(bigImg,
                    new Dimension(100, 100));
            byte[] buff = ImgTools.imgToByteArray(thumbnailImage);
            PreparedStatement prep;
            prep = connection.prepareStatement(
                    "update IMAGES set thumb=? where _rowid_ = " + id);
            // "update IMAGES set thumb=? where thumb = null and _rowid_ = "+id);

            prep.setBytes(1, buff);
            prep.execute();
            //connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void changeBigImg(BufferedImage img, int id) {
        try {
            img = ImgTools.removeAlpha(img);
            byte[] buff = ImgTools.imgToByteArray(img);
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

    /**
     * Put raw data into IMAGES table
     * @param img image as byte array
     * @param thumb thumbnail as byte array
     * @param name record name
     */
    private static void stat_insertImageRecord(byte[] img, byte[] thumb, String name) {
        PreparedStatement prep;
        HashingAlgorithm hasher = new PerceptiveHash(32);
        Hash hash0 = hasher.hash(byteArrayToImg(img));
        try {
            prep = connection.prepareStatement(
                    "insert into IMAGES (image,thumb,name,imghash) values (?,?,?,?)");
            prep.setBytes(1, img);
            prep.setBytes(2, thumb);
            prep.setString(3, name);
            prep.setObject(4, hash0);
            prep.execute();
        } catch (SQLException e) {
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

    public static String querySingleValue(String sql) {
        ResultSet res = query(sql);
        if (res == null)
            throw new RuntimeException("no query results");
        try {
            if (res.next()) {
                String len = res.getString(1);
                res.close();
                return len;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("no query results");
    }

    public static SoftReference<byte[]> loadBytes(String sql) throws Exception {
        //String filename = nid.name.replace("'", "''");
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

    public static String queryBlobLen(DBHandler.NameID nid, String table, String blobentry) {
        String s = querySingleValue("select BLOBSIZE from " + table + " where _ROWID_='" + nid.rowid + "'");
        if (s == null) {
            s = querySingleValue("select LENGTH(" + blobentry + ") from " + table + " where _ROWID_='" + nid.rowid + "'");
            execSQL("update " + table + " set BLOBSIZE=" + s + " where _ROWID_='" + nid.rowid + "'");
        }
        return s;
    }

    public static SoftReference<byte[]> loadVideoBytes(DBHandler.NameID nid) throws Exception {
        return loadBytes("select VID from VIDEOS where _ROWID_='" + nid.rowid + "'");
    }

    public static SoftReference<byte[]> loadGifBytes(DBHandler.NameID nid) throws Exception {
        return loadBytes("select GIFDATA from GIFS where _ROWID_='" + nid.rowid + "'");
    }

    public static SoftReference<byte[]> loadWEBPBytes(DBHandler.NameID nid) throws Exception {
        return loadBytes("select WEBPDATA from WEBP where _ROWID_='" + nid.rowid + "'");
    }

    public static String getVideoBlobLen(DBHandler.NameID nid) {
        return queryBlobLen(nid, "VIDEOS", "VID");
    }

    public static String getGifBlobLen(DBHandler.NameID nid) {
        return queryBlobLen(nid, "GIFS", "GIFDATA");
    }

    public static String getWEBPBlobLen(DBHandler.NameID nid) {
        return queryBlobLen(nid, "WEBP", "WEBPDATA");
    }

    public static void cancelFileTransfer() {
        transferTask.cancel(true);
    }

    public static File transferIntoFile(DBHandler.NameID nid, String type) throws Exception {
        AtomicReference<File> f = new AtomicReference<>();
        transferTask = Tools.runTask(() -> {
            try {
                f.set(transferIntoFileInternal(nid, type));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        transferTask.get(); // wait
        return f.get();
    }

    /**
     * Load DB record into mapped file
     * @return file name of file on disk
     * @throws Exception if smth gone wrong
     */
    private static File transferIntoFileInternal(DBHandler.NameID nid, String type) throws Exception {
        SoftReference<byte[]> bt = switch (type) {
            case "GIF" -> loadGifBytes(nid);
            case "WEBP" -> loadWEBPBytes(nid);
            default ->  // regular vid
                    loadVideoBytes(nid);
        };
        File fi = new File(System.getProperty("java.io.tmpdir") + File.separator + "tempfile-" + "myra.dat");
        fi.deleteOnExit();
        try (RandomAccessFile rafile = new RandomAccessFile(fi, "rw")) {
            MappedByteBuffer out = rafile.getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, 0, Objects.requireNonNull(bt.get()).length);
            out.put(Objects.requireNonNull(bt.get()));
            out.load();
        }
        return fi;
    }


    public static File transferGifIntoFile(DBHandler.NameID nid) throws Exception {
        return transferIntoFile(nid, "GIF");
    }

    public static File transferwEBPIntoFile(DBHandler.NameID nid) throws Exception {
        return transferIntoFile(nid, "WEBP");
    }


    public static File transferVideoIntoFile(DBHandler.NameID nid) throws Exception {
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

    public static void changeName(String table, String name, int rowid) {
        String sql = "update " + table + " set name ='" + name + "' where _rowid_ =" + rowid;
        try {
            statement.execute(sql);
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

    public static synchronized void incAccCounter(int rowid) {
        String sql = "update IMAGES set ACCNUM = (ACCNUM + 1) where _rowid_ =" + rowid;
        try {
            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void setAccCounter(int rowid, int val) {
        String sql = "update IMAGES set ACCNUM = " + val + " where _rowid_ =" + rowid;
        try {
            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized int getAccCounter(int rowid) {
        String q = "select ACCNUM from IMAGES where _rowid_ =" + rowid;
        try (ResultSet res = query(q)) {
            if (Objects.requireNonNull(res).next()) {
                System.out.println("readACC: " + rowid);
                int ret = res.getInt(1);
                // init with 1 on first use
                if (ret == 0) {
                    setAccCounter(rowid, 1);
                    return 1;
                }
                return ret;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public static class HashId {
        public Hash hash;
        public int rowID;
        public HashId (Hash h, int r) {
            hash = h;
            rowID = r;
        }
    }

    public static ArrayList<HashId> loadPerceptiveImgHashes() {
        String q = "select imghash,_rowid_ from IMAGES where imghash is not null";
        ArrayList<HashId> list = new ArrayList<>();
        try {
            try (ResultSet res = query(q)) {
                while (Objects.requireNonNull(res).next()) {
                    HashId hid = new HashId((Hash)res.getObject(1), res.getInt(2));
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
                Hash hash = (Hash)res.getObject(2);
                if (hash == null) { // create hash if missing
                    BufferedImage bi = byteArrayToImg(img);
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
            //System.out.println(e);
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Hash getPerceptiveHash(int rowid) {
        try (ResultSet res = query("select imghash from images where _rowid_ = " + rowid)) {
            try {
                assert res != null;
                res.next();
                return (Hash) res.getObject(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ///   ///////////////////////
/*
    public static void main(String[] args) throws Exception {
        ResultSet res = query("select _rowid_ from images where imghash is null");
        HashingAlgorithm hasher = new PerceptiveHash(32);
        ArrayList<Integer> al = new ArrayList<>();
        while (res.next()) {
            al.add(res.getInt(1));
        }
        Collections.sort(al);
        for (Integer rowid : al) {
            res = query("select image from images where _rowid_ = " + rowid);
            res.next();
            BufferedImage img = byteArrayToImg(res.getBytes(1));
            if (img != null) {
                Hash hash0 = hasher.hash(img);
                PreparedStatement prep = connection.prepareStatement(
                        "update IMAGES set imghash=? where _rowid_ = " + rowid);
                prep.setObject(1, hash0);
                prep.execute();
            }
            if (rowid%100 == 0)
                System.out.println(rowid);
        }
    }
*/
// / /////////////////////////

    public record NameID(String name, int rowid, String tag) {
        @Override
        public String toString() {
            return name + " : (" + rowid + ") ";
        }
    }

    public record LogMessage(String time, String entry) {
        @Override
        public String toString() {
            return time + " : " + entry + "\n";
        }
    }

}
