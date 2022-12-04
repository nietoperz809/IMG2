package database;

import thegrid.ImageScaler;
import thegrid.Tools;
import thegrid.UnlockDBDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBHandler {

    private static final String rootDir = "C:\\peter.home\\java\\IMG2\\datastore\\";
    private static final String dbFile = "mydb";
    private static final String dbFileFull = "mydb" + ".mv.db";
    private static String aes_pwd = null;
    private static DBHandler _inst = null;
    private Connection connection;
    private Statement statement;
    /*
        jdbc:h2:C:\peter.home\java\IMG2\datastore\mydb;CIPHER=AES
     */

    /**
     * Private constructor like Singletons should have
     */
    private DBHandler() {
        try {
            if (aes_pwd == null) {
                aes_pwd = UnlockDBDialog.xmain();
            }
            String url = "jdbc:h2:" + rootDir + dbFile + ";CIPHER=AES";
            String user = "LALA";
            String pwd = aes_pwd + " dumm";
            connection = DriverManager.getConnection(url, user, pwd);
            statement = connection.createStatement();
            // create video table
            String sql = "create table if not exists VIDEOS " +
                    "(VID blob, NAME varchar(200), HASHVAL blob(16))";
            statement.execute(sql);
        } catch (SQLException e) {
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
        return _inst;
    }

    /**
     * Warning box if an image is about to be deleted
     *
     * @param f Reference to parent component
     * @return true if user clicked OK
     */
    static boolean askForDel(Component f, String imgName) {
        Object[] options = {"OK", "NO! NEVER!!"};
        return JOptionPane.showOptionDialog(f,
                "Delete " + imgName + " from DB?",
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[1]
        ) == 0;
    }

    public Connection getConn() {
        return connection;
    }

    private ResultSet query(String txt) {
        try {
            return statement.executeQuery(txt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<NameID> getFileNames() {
        ArrayList<NameID> al = new ArrayList<>();
        try (ResultSet res = query("select name,_ROWID_ from IMAGES order by _ROWID_ asc")) {
            while (res.next()) {
                al.add(new NameID(res.getString(1),
                        res.getInt(2)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return al;
        //return al.subList(0,10);
    }

    public boolean delete(int rowid) {
        if (!askForDel(null, "" + rowid)) {
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

    public void backup() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
                .format(new java.util.Date());
        String newFile = rootDir + timeStamp + ".backup";
        String origFile = rootDir + dbFileFull;

        close();
        try {
            InputStream in = new BufferedInputStream(
                    new FileInputStream(origFile));

            OutputStream out = new BufferedOutputStream(
                    new FileOutputStream(newFile));

            byte[] buffer = new byte[1024*1024*4];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                System.out.print(".");
                out.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getInst(); // reopen
    }

    public void addImages(File[] files, InsertCallback ic) throws Exception {
        for (File file : files) {
            String name = UUID.randomUUID().toString();
            BufferedImage img = Tools.loadImage(file.getPath());
            if (img == null) {
                System.err.println("no image");
                continue;
            }
            insert(name, img);
            ic.newImage(img, name);
        }
        connection.commit();
    }

    public void insert(String name, BufferedImage img) throws IOException {
        byte[] buff = Tools.imgToByteArray(img);
        BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                new Dimension(100, 100));
        byte[] buff2 = Tools.imgToByteArray(thumbnailImage);
        addImage(buff, buff2, name);
    }

    private void addImage(byte[] img, byte[] thumb, String name) {
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

    public BufferedImage loadImage(String filename) throws IOException {
        try (ResultSet res = query("select image from IMAGES where name = '" + filename + "'")) {
            if (res.next()) {
                byte[] b = res.getBytes(1);
                return Tools.byteArrayToImg(b);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ThumbHash loadThumbnail(String filename) throws IOException {
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

    public void close() {
        try {
            connection.close();
            _inst = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class NameID {
        public final String name;
        public final int rowid;

        public NameID(String name, int rowid) {
            this.name = name;
            this.rowid = rowid;
        }
    }

    public static class ThumbHash {
        public final BufferedImage img;
        public final byte[] hash;

        public ThumbHash(byte[] bytes) {
            try {
                img = Tools.byteArrayToImg(bytes);
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(bytes);
                hash = md.digest();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
