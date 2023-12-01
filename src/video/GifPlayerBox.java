package video;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class GifPlayerBox {

//    public void start (byte[] gifdata) {
//        JLabel playerLabel = new JLabel();
//        ImageIcon icon = new ImageIcon(gifdata);
//        int width = 600;
//        int height = 600;
//        icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
//        playerLabel.setIcon(icon);
//
//        JFrame playerFrame;
//        playerFrame = new JFrame();
//        playerFrame.requestFocus();
//        playerFrame.setTitle("GifPlayer");
//        playerFrame.setBounds(100, 100, width, height);
//        playerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        playerFrame.setLayout(new BorderLayout());
//        playerFrame.add(playerLabel, BorderLayout.CENTER); //setContentPane(mpc);
//        playerFrame.setVisible(true);
//
//    }

    boolean shouldClose = false;

    public void start(String path) {
        GifDecoder d = new GifDecoder();
        d.read (path);
        //d.read("C:\\Users\\Administrator\\Desktop\\pcar.gif");
        int num = d.getFrameCount();
        System.out.println(num);

        JLabel label = new JLabel();
        JFrame window = new javax.swing.JFrame();
        window.setBackground(Color.orange);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
        window.getContentPane().add(label, BorderLayout.CENTER);
        window.setSize(600, 600);
        window.setVisible(true);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("shouldClose");
                shouldClose = true;
                window.dispose();
            }
        });

        new Thread (() -> {
                    while (true) {
                        for (int i = 0; i < num; i++) {
                            BufferedImage frame = d.getFrame(i);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ioe) {

                            }
                            ImageIcon icon = new ImageIcon(frame.getScaledInstance
                                            (label.getWidth(), label.getHeight(),
                                                    Image.SCALE_DEFAULT));
                            label.setIcon(icon);
                            if (shouldClose) {
                                System.out.println("end thread");
                                return;
                            }
                        }
                    }
                }).start();

    }
}
