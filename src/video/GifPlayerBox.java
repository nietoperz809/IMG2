package video;

import common.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class GifPlayerBox {

    AtomicBoolean bflag = new AtomicBoolean(false);

    public GifPlayerBox (String path) {
        GifDecoder d = new GifDecoder();
        d.read(path);
        //d.read("C:\\Users\\Administrator\\Desktop\\pcar.gif");
        int num = d.getFrameCount();
        System.out.println(num);

        JLabel label = new JLabel();
        JFrame window = new javax.swing.JFrame();
        window.setBackground(Color.orange);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.getContentPane().add(label, BorderLayout.CENTER);
        window.setSize(600, 600);
        window.setVisible(true);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("shouldClose");
                bflag.set(true);
                window.dispose();
            }
        });

        new Thread(() -> {
            while (!bflag.get()) {
                for (int i = 0; i < num && !bflag.get(); i++) {
                    BufferedImage frame = d.getFrame(i);
                    Image im2 = frame.getScaledInstance (label.getWidth(), label.getHeight(),
                                    Image.SCALE_DEFAULT);
                    label.setIcon(new ImageIcon(im2));
                    Tools.delay(150);
                }
            }
            System.out.println("end thread");
        }).start();
    }
}
