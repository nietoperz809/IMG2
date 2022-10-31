package imageloader;

import thegrid.ImageScaler;
import thegrid.Tools;
import thegrid.UnlockDBDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBHandler {

    private static String aes_pwd = null;
    private Connection connection;
    private Statement statement;

    private static DBHandler _inst = null;

    /**
     * get access to the DB
     * @return the one and only DB handler
     */
    public static DBHandler getInst() {
        if (_inst == null) {
            _inst = new DBHandler();
        }
        return _inst;
    }

    /**
     * Private constructor like Singletons should have
     */
    private DBHandler() {
        try {
            if (aes_pwd == null) {
                aes_pwd = UnlockDBDialog.xmain();
            }
            String url = "jdbc:h2:C:\\peter.home\\java\\IMG2\\datastore\\mydb;CIPHER=AES";
            String user = "LALA";
            String pwd = aes_pwd + " dumm";
            connection = DriverManager.getConnection(url, user, pwd);
            statement = connection.createStatement();
        } catch (SQLException e) {
            Tools.Error("Wrong Password!");
            System.exit(-1);
        }
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

    private ResultSet query(String txt) {
        try {
            return statement.executeQuery(txt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getFileNames() {
        ArrayList<String> al = new ArrayList<>();
        try (ResultSet res = query("select name from IMAGES")) {
            while (res.next()) {
                al.add(res.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return al;
    }

    public boolean delete(String name) {
        if (!askForDel(null, name)) {
            return false;
        }
        try {
            statement.execute("delete from IMAGES where name = '" + name + "'");
            statement.execute("delete from THUMBS where name = '" + name + ".jpg'");
            return true;
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    public void addImages(File[] files, InsertCallback ic) throws Exception {
        for (File file : files) {
            String name = UUID.randomUUID().toString();
            BufferedImage img = Tools.loadImage(file.getPath());
            insert(name, img);
            ic.newImage(img, name);
        }
        connection.commit();
    }

    public void insert(String name, BufferedImage img) throws IOException {
        byte[] buff = Tools.imgToByteArray(img);
        addImage(buff, name);
        BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                new Dimension(100, 100));
        buff = Tools.imgToByteArray(thumbnailImage);
        addThumb(buff, name + ".jpg");
    }

    private void addImage(byte[] img, String name) {
        PreparedStatement prep;
        try {
            prep = connection.prepareStatement(
                    "insert into IMAGES (image,name) values (?,?)");
            prep.setBytes(1, img);
            prep.setString(2, name);
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addThumb(byte[] img, String name) {
        PreparedStatement prep;
        try {
            prep = connection.prepareStatement(
                    "insert into THUMBS (thumb,name) values (?,?)");
            prep.setBytes(1, img);
            prep.setString(2, name);
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

    public BufferedImage loadThumbnail(String filename) throws IOException {
        String q = "select thumb from THUMBS where name = '" + filename + ".jpg'";
        try (ResultSet res = query(q)) {
            if (res.next()) {
                byte[] b = res.getBytes(1);
                return Tools.byteArrayToImg(b);
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

//    public static void main(String[] args) throws SQLException, IOException {
//
//        DBHandler db = new DBHandler();
//        List<String> names = db.getFileNames();
//
//        for (String name : names) {
//            BufferedImage img = db.loadImage(name);
//            BufferedImage thumb = db.loadThumbnail(name);
//            System.out.println(name);
//        }
//
//
//
//        //db.statement.execute("drop table imggrid");
////        db.statement.execute("create table imggrid (name varchar(200) not null primary key," +
////                " thumb blob, image blob)");
//
////        ResultSet rs = db.query("select CONCAT_WS('.', '"+names.get(0)+"', 'jpg')");
////        rs.next();
////        System.out.println(rs.getString(1));
//
//    }
}
