package database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;

/*

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

 */

public class VideoFiller {

//    public static void main(String[] args) throws Exception {
//        File[] files = new File("C:\\Users\\Administrator\\Desktop\\porn").listFiles();
//        DBHandler db = DBHandler.getInst();
//        for (File file : files) {
//            if (file.isFile()) {
//                System.out.println(file.getName());
//                byte[] fileContent = Files.readAllBytes(file.toPath());
//
//                PreparedStatement prep = db.getConn().prepareStatement(
//                        "insert into VIDEOS (vid,name) values (?,?)");
//                prep.setBytes(1, fileContent);
//                prep.setString(2, file.getName());
//                prep.execute();
//
//                System.out.println(fileContent.length);
//            }
//        }
//    }

}

