package video;

import common.ImgTools;
import common.Tools;
import thegrid.ImageScaler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GifPlayerBox {

    private final AtomicBoolean stopFlag = new AtomicBoolean(false);

    private final AtomicBoolean waitFlag = new AtomicBoolean(false);

    private final AtomicReference<Image> currentFrame = new AtomicReference<>();

    private boolean reverse = false;

    private final AtomicInteger sleepTime = new AtomicInteger(150);

    public GifPlayerBox(File file, VideoApp parent) {
        GifDecoder d = new GifDecoder();
        d.read(file.getAbsolutePath());
        int frameCount = d.getFrameCount();
        System.out.println(frameCount);

        JLabel label = new JLabel();
        JFrame window = new javax.swing.JFrame();
        window.setBackground(Color.orange);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.getContentPane().add(label, BorderLayout.CENTER);
        window.setSize(600, 600);
        window.setVisible(true);
        window.setTitle("(p)photo (r)reverse, (+/-)faster/slower (s)wait");
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("shouldClose");
                stopFlag.set(true);
                window.dispose();
                file.delete();
            }
        });

        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                switch (keyEvent.getKeyChar()) {
                    case 'r':
                        reverse = !reverse;
                        break;
                    case 's':
                        waitFlag.set(!waitFlag.get());
                        break;
                    case 'p':
                        try {
                            //BufferedImage scaled = ImageScaler.scaleExact(currentFrame.get(), new Dimension(800, 800));
                            ImageIO.write(ImgTools.toBufferedImage(currentFrame.get()), "png",
                                    new File(parent.snapDir + File.separator + System.currentTimeMillis() + ".png"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case '+':
                        sleepTime.getAndAdd(-100);
                        if (sleepTime.get() < 0) sleepTime.set(0);
                        break;
                    case '-':
                        sleepTime.getAndAdd(100);
                        if (sleepTime.get() > 5000) sleepTime.set(5000);
                        break;
                }
            }
        });

        new Thread(() -> {
            while (!stopFlag.get()) {
                for (int i = 0; i < frameCount && !stopFlag.get(); i++) {
                    if (!waitFlag.get()) {
                        currentFrame.set(d.getFrame(reverse ? frameCount - 1 - i : i));
                        Image im2 = currentFrame.get().getScaledInstance(label.getWidth(), label.getHeight(),
                                Image.SCALE_DEFAULT);
                        currentFrame.set(im2);
                        label.setIcon(new ImageIcon(im2));
                        Tools.delay(sleepTime.get());
                    }
                }
            }
            System.out.println("end thread");
        }).start();
    }
}
