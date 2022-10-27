package imageloader;

import thegrid.Tools;
import thegrid.UnlockDBDialog;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLReader extends ImageStore {

    private Connection connection;
    private Statement statement;

    public SQLReader(String root) {
        super(root);
        try {
            String aes_pwd = UnlockDBDialog.xmain();
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
