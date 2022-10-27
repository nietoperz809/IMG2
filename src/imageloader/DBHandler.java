package imageloader;

import thegrid.ImageScaler;
import thegrid.Tools;
import thegrid.UnlockDBDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends ImageStore {

    private Connection connection;
    private Statement statement;

    private static String aes_pwd = null;

    public DBHandler(String root) {
        super(root);
        try {
            if (aes_pwd == null) {
                aes_pwd = UnlockDBDialog.xmain();
            }
            String url = "jdbc:h2:C:\\peter.home\\java\\IMG2\\datastore\\mydb;CIPHER=AES";
            String user = "LALA";
            String pwd = aes_pwd +" dumm";
            connection = DriverManager.getConnection(url, user, pwd);
            statement = connection.createStatement();
            //statement.execute("CREATE USER LALA PASSWORD 'dumm' ADMIN\n");
        } catch (SQLException e) {
            System.exit(-1);
        }
    }

    private ResultSet query(String txt) {
        try {
            return statement.executeQuery(txt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
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

    /**
     * Warning box if a single frame is about to be disposed
     * @param f Reference to parent component
     * @return  true if user clicked OK
     */
    static boolean askForDel(Component f, String imgname)
    {
        Object[] options = {"OK", "NO! NEVER!!"};
        return JOptionPane.showOptionDialog(f,
                                            "Delete "+imgname+" from DB?",
                                            "Warning",
                                            JOptionPane.DEFAULT_OPTION,
                                            JOptionPane.WARNING_MESSAGE, null, options, options[1]
        ) == 0;
    }

    public boolean delete (String name) {
        if (!askForDel(null, name)) {
            return false;
        }
        try {
            statement.execute("delete from IMAGES where name = '"+name+"'");
            statement.execute("delete from THUMBS where name = '"+name+".jpg'");
            return true;
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    @Override
    public boolean insert(String name, BufferedImage img) {
        try {
            byte[] buff = Tools.imgToByteArray(img);
            addImage(buff, name);
            BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                    new Dimension(100, 100));
            buff = Tools.imgToByteArray(thumbnailImage);
            addThumb(buff, name+".jpg");
            return true;
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    private void addImage (byte[] img, String name) {
        PreparedStatement prep = null;
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

    private void addThumb (byte[] img, String name) {
        PreparedStatement prep = null;
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


    @Override
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

    @Override
    public void close() throws IOException {

    }
}
