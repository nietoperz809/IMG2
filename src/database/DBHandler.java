package database;

import common.*;
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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DBHandler {
    private static final String ROOT_DIR = "C:\\Databases\\";
    private static final String NO_PASS = "NoPass";
    private static final String DB_FILE = "mydb";
    private static final String DB_FILE_FULL = DB_FILE + ".mv.db";
    private static DBHandler _inst = null;
    private Connection connection;
    private Statement statement;
    private volatile boolean _backupIsRunning;
    /*
        jdbc:h2:C:\peter.home\java\IMG2\datastore\mydb;CIPHER=AES
     */

    /**
     * Private constructor like Singletons should have
     */
    private DBHandler() {
        PersistString pers = new PersistString("pwddb", NO_PASS);
        try {
            String aes_pwd;
            if (pers.get().equals(NO_PASS)) {
                aes_pwd = UnlockDBDialog.xmain();
                pers.set(aes_pwd);
            }
            else {
                aes_pwd = pers.get();
            }
            String url = "jdbc:h2:" + ROOT_DIR + DB_FILE + ";CIPHER=AES";
            String user = "LALA";
            String pwd = aes_pwd + " dumm";
            connection = DriverManager.getConnection(url, user, pwd);
            statement = connection.createStatement();
            Sam.speak("deta base is on line.");
            String sql;
            // create log table
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
            sql = "alter table IMAGES add if not exists TAG varchar(128)";
            statement.execute(sql);
            sql = "alter table VIDEOS add if not exists TAG varchar(128)";
            statement.execute(sql);
        } catch (SQLException e) {
            Sam.speak("Failed to connect to data base");
            pers.reset();
            Tools.Error(e.toString());
            System.exit(-1);
        }
    }

    /**
     * get access to the DB
     *
     * @return the one and only DB handler
     */
    public static DBHandler getInst() {
        if (_inst == null) {
            _inst = new DBHandler();
        }
        if (_inst._backupIsRunning) {
            Sam.speak("backup is running.");
            return null;
        }
        return _inst;
    }

    public void close() {
        try {
            connection.close();
            //_inst = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Warning box if an image is about to be deleted
     *
     * @return true if user clicked OK
     */
    static boolean askForDel(String imgName) {
        Object[] options = {"OK", "NO! NEVER!!"};
        return JOptionPane.showOptionDialog(null,
                "Delete " + imgName + " from DB?",
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[1]
        ) != 0;
    }

//    public Connection getConn() {
//        return connection;
//    }

    public synchronized ResultSet query(String txt) {
        try {
            return statement.executeQuery(txt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void log (String str) {
        try {
            statement.execute("insert into LOG(entry) values ('"+str+"')");
        } catch (SQLException e) {
            System.out.println(e);
            //throw new RuntimeException(e);
        }
    }

    public void reduceLog () {
        try {
            statement.execute("delete from log where _rowid_ < (select max (_rowid_)-50 from log)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<LogMessage> getLog() {
        String sql = "select * from LOG order by ltime";
        ArrayList<LogMessage> al = new ArrayList<>();
        try (ResultSet res = query(sql)) {
            while (res.next()) {
                al.add(new LogMessage(res.getString(1),
                        res.getString(2)));
            }
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        return al;

    }


    public List<NameID> getAllImageInfos() {
        String sql = "select name,_ROWID_,tag from IMAGES order by _ROWID_ asc";
        return getNames(sql);
    }

    public List<NameID> getFileNames (String dbname, boolean sort_by_id) {
        String sort = sort_by_id ? "_ROWID_" : "name";
        String sql = "select name,_ROWID_,tag from "+dbname+" order by "+sort+" asc";
        return getNames(sql);
    }

    public List<NameID> getVideoFileNames(boolean sort_by_id) {
        return getFileNames("VIDEOS", sort_by_id);
    }

    public List<NameID> getGifFileNames(boolean sort_by_id) {
        return getFileNames("GIFS", sort_by_id);
    }

    private synchronized List<NameID> getNames(String sql) {
        ArrayList<NameID> al = new ArrayList<>();
        try (ResultSet res = query(sql)) {
            while (res.next()) {
                al.add(new NameID(res.getString(1),
                        res.getInt(2),
                        res.getString(3)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return al;
    }

    public boolean deleteImage (int rowid) {
        if (askForDel(String.valueOf(rowid))) {
            return false;
        }
        try {
            statement.execute("delete from IMAGES where _ROWID_ = " + rowid);
            return true;
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    public boolean deleteVideo (int rowid) {
        return deleteGifOrVideo ("VIDEOS", rowid);
    }

    public boolean deleteGif (int rowid) {
        return deleteGifOrVideo ("GIFS", rowid);
    }


    public boolean deleteGifOrVideo (String dbname, int rowid) {
        if (askForDel(String.valueOf(rowid))) {
            return false;
        }
        try {
            statement.execute("delete from "+dbname+" where _ROWID_ = " + rowid);
            return true;
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    public void setTag (int rowid, String tag) {
        try {
            statement.execute("update IMAGES set tag = '"+tag+"' where _ROWID_ = " + rowid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTag (int rowid) {
        String strres = null;
        try {
            try (ResultSet res = query("select tag from IMAGES where _ROWID_ = " + rowid)) {
                if (res.next()) {
                    strres = res.getString(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return strres;
    }

    public ArrayList<String> getTagList() {
        ArrayList<String> list = new ArrayList<>();
        try {
            try (ResultSet res = query("select distinct tag from IMAGES")) {
                while (res.next()) {
                    String s = res.getString(1);
                    if (s != null)
                        list.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void backup() {
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
                final byte[] buffer = new byte[1024*1024*4];
                int lengthRead;
                long total = 0;
                _backupIsRunning = true;
                while ((lengthRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, lengthRead);
                    total += lengthRead;
                    System.out.print("."+total);
                    //Thread.yield();
                    //System.out.print(".");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                _backupIsRunning = false;
                _inst = null;
                Instant end = Instant.now();
                String msg = "DB backup took: " + Duration.between(startTime, end).toSeconds() + " Seconds";
                Tools.Info(msg);
                System.out.println("done!");
                try {
                    Objects.requireNonNull(out).close();
                    Objects.requireNonNull(in).close();
                } catch (IOException e) {
                    System.out.println(e);
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
    public int MoveImageFilesToDB (File[] files, InsertCallback ic) throws Exception {
        int ret = 0;
        for (File file : files) {
            String name = UUID.randomUUID().toString();
            BufferedImage img = ImgTools.loadImage(file.getPath());
            if (img == null) {
                System.err.println("no image");
                continue;
            }
            insertImageRecord(name, img);
            ic.justInserted(img, name);
            if (file.delete())
                ret++;
        }
        connection.commit();
        return ret;
    }

    /**
     * Convert img into INT_RGB, generate thumbnail and put all int the tabke
     * @param name image name, can be any string
     * @param img th image
     * @throws IOException if smth. gone wrong
     */
    public void insertImageRecord (String name, BufferedImage img) throws IOException {
        BufferedImage big = ImageScaler.scaleExact(img,
                new Dimension (img.getWidth(), img.getWidth()));
        byte[] buff = ImgTools.imgToByteArray(big);
        BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                new Dimension(100, 100));
        byte[] buff2 = ImgTools.imgToByteArray(thumbnailImage);
        insertImageRecord(buff, buff2, name);
    }

    public void changeBigImg (BufferedImage img, int id) {
        try {
            img = ImgTools.removeAlpha(img);
            byte[] buff = ImgTools.imgToByteArray(img);
            PreparedStatement prep;
            prep = connection.prepareStatement(
                    "update IMAGES set image=? where _rowid_ = "+id);
            prep.setBytes(1, buff);
            prep.execute();
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addVideoFile (File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            insertVideoRecord(fileContent, file.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addGifFile (File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            insertGifRecord(fileContent, file.getName());
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
    private void insertImageRecord (byte[] img, byte[] thumb, String name) {
        PreparedStatement prep;
        try {
            prep = connection.prepareStatement(
                    "insert into IMAGES (image,thumb,name) values (?,?,?)");
            prep.setBytes(1, img);
            prep.setBytes(2, thumb);
            prep.setString(3, name);
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertVideoRecord (byte[] vid, String name) {
        PreparedStatement prep;
        try {
            prep = connection.prepareStatement(
                    "insert into VIDEOS (vid,name) values (?,?)");
            prep.setBytes(1, vid);
            prep.setString(2, name);
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertGifRecord (byte[] gif, String name) {
        PreparedStatement prep;
        try {
            prep = connection.prepareStatement(
                    "insert into GiFS (gifdata,name) values (?,?)");
            prep.setBytes(1, gif);
            prep.setString(2, name);
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public SoftReference<byte[]> loadVideoBytes (String filename) throws Exception {
        filename = filename.replace("'", "''");
        ResultSet res = DBHandler.getInst()
                .query("select VID from VIDEOS where name='"+filename+"'");
        if (res == null)
            throw new RuntimeException("no query results");
        if (res.next()) {
            SoftReference<byte[]> bt = new SoftReference<>(res.getBytes(1));
            res.close();
            return bt;
        }
        throw new RuntimeException("no query results");
    }

    public byte[] loadGifBytes (String filename) throws Exception {
        filename = filename.replace("'", "''");
        ResultSet res = DBHandler.getInst()
                .query("select GIFDATA from GIFS where name='"+filename+"'");
        if (res == null)
            throw new RuntimeException("no query results");
        if (res.next()) {
            byte[] bt = res.getBytes(1);
            res.close();
            return bt;
        }
        throw new RuntimeException("no query results");
    }

    /**
     * Load video into mapped file
     * @param videoName name of video record in database
     * @return file name of file on disk
     * @throws Exception if smth gone wrong
     */
    public String transferVideoIntoFile (String videoName) throws Exception {
        SoftReference<byte[]> bt = loadVideoBytes(videoName);
        File fi = new File(System.getProperty("java.io.tmpdir")+File.separator+"myra.dat");
        try (RandomAccessFile rafile = new RandomAccessFile(fi, "rw")) {
            MappedByteBuffer out = rafile.getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, 0, bt.get().length);
            out.put (bt.get());
            out.load();
        }
        return fi.getAbsolutePath();
    }

    public void changeVideoName (String name, int rowid) {
        String sql = "update VIDEOS set name ='"+name+"' where _rowid_ ="+rowid;
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public byte[] loadThumbnail (int rowid) {
        String q = "select thumb from IMAGES where _rowid_ =" + rowid;
        try (ResultSet res = query(q)) {
            if (res.next()) {
                return res.getBytes(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public byte[] loadImage (int rowid) {
        String q = "select image from IMAGES where _rowid_ =" + rowid;
        try (ResultSet res = query(q)) {
            if (res.next()) {
                return res.getBytes(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        return null;
    }

    public ThumbHash loadThumbnail(String filename) {
        String q = "select thumb from IMAGES where name = '" + filename + "'";
        try (ResultSet res = query(q)) {
            if (res.next()) {
                return new ThumbHash(res.getBytes(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public record NameID(String name, int rowid, String tag) {
        @Override
            public String toString() {
                return name + " : (" + rowid + ")";
            }
        }

    public record LogMessage(String time, String entry) {
        @Override
        public String toString() {
            return time + " : " + entry+"\n";
        }
    }

    public static class ThumbHash {
        public final BufferedImage img;
        public final byte[] hash;

        public ThumbHash(byte[] bytes) {
            try {
                img = ImgTools.byteArrayToImg(bytes);
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(bytes);
                hash = md.digest();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
