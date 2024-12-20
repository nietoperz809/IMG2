package video;

import common.ImgTools;
import common.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AnimPlayerBox implements PlayerBox {

    private final AtomicBoolean stopFlag = new AtomicBoolean(false);
    private final AtomicBoolean waitFlag = new AtomicBoolean(false);
    //private final AtomicReference<Image> currentFrame = new AtomicReference<>();
    private final boolean autoclose;
    private final VideoApp parent;
    private boolean reverse = false;
    private final AtomicInteger sleepTime = new AtomicInteger(100);
    private boolean saveFlag;
    private final JFrame window;
    private final File file;

    public AnimPlayerBox (File file, VideoApp parent, AnimDecoder decoder,
                          boolean close_when_finished) {
        autoclose = close_when_finished;
        this.file = file;
        this.parent = parent;
        decoder.read(file.getAbsolutePath());
        int frameCount = decoder.getFrameCount();
        System.out.println(frameCount);

        JLabel label = new JLabel();
        window = new javax.swing.JFrame();
        window.setBackground(Color.orange);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.getContentPane().add(label, BorderLayout.CENTER);
        window.setSize(600, 600);
        window.setVisible(true);
        window.setTitle("(p)photo (r)reverse, (+/-)faster/slower (s)wait");
        window.setLocationRelativeTo(null); // center on screen

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop();
            }
            @Override
            public void windowClosed(WindowEvent e) {
                parent.clientDisposed();
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
                        saveFlag = !saveFlag;
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

        Tools.runTask(() -> {
            ImageIcon iic = new ImageIcon();
            label.setIcon(iic);
            while (!stopFlag.get()) {
                for (int frameNum = 0; frameNum < frameCount && !stopFlag.get(); frameNum++) {
                    if (!waitFlag.get()) {
                        Image im2 = decoder.getFrame(reverse ? frameCount - 1 - frameNum : frameNum)
                            .getScaledInstance(label.getWidth(), label.getHeight(),Image.SCALE_DEFAULT);
                        if (saveFlag) {
                            ImgTools.writeToFile(im2, "jpg", parent.snapDir,""+frameNum);
                        }
                        iic.setImage(im2);
                        label.repaint();
                        //label.setIcon(new ImageIcon(im2));
                        Tools.delay(sleepTime.get());
                    }
                }
                if (autoclose)
                    stop();
            }
            System.out.println("end thread");
        });
    }

    @Override
    public void start() {

    }

    public void stop() {
        stopFlag.set(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                window.dispose();
                file.delete();
            }
        });
    }
}
