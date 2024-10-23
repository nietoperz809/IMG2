//package database;
//
//
//import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
//
//import javax.swing.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.sql.ResultSet;
//
//public class VideoFiller {
//
//    // fill video table
//
//    public static void main(String[] args) throws Exception {
//        ResultSet res = DBHandler.getInst()
//                .query("select VID from VIDEOS where _ROWID_=1");
//        if (res == null)
//            return;
//        Path temp = Files.createTempFile("vid_", ".tmp");
//        if (res.next()) {
//            byte[] b = res.getBytes(1);
//            Files.write(temp, b);
//        }
//        res.close();
//        System.out.println(temp);
//
//        JFrame frame = new JFrame("My First Media Player");
//        frame.setBounds(100, 100, 600, 400);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        EmbeddedMediaPlayerComponent mpc
//                = new EmbeddedMediaPlayerComponent();
//        frame.setContentPane(mpc);
//        frame.setVisible(true);
//
//        mpc.mediaPlayer().videoSurface().attachVideoSurface();
//
//        mpc.mediaPlayer().media().play(temp.toString());
//
//    }
//
//}
//
