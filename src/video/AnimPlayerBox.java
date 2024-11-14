package video;

import common.ImgTools;
import common.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static video.AnimDecoder.stopImage;

public class AnimPlayerBox implements PlayerBox {

    private final AtomicBoolean stopFlag = new AtomicBoolean(false);
    private final AtomicBoolean waitFlag = new AtomicBoolean(false);
    //private final AtomicReference<Image> currentFrame = new AtomicReference<>();
    private final boolean autoclose;
    private final VideoApp parent;
    private final AtomicInteger sleepTime = new AtomicInteger(100);
    private final JFrame window;
    private final File file;
    private boolean reverse = false;
    private boolean saveFlag;
    private BlockingQueue<BufferedImage> __que = new ArrayBlockingQueue<>(20);
    private Future decoderTask = null;

    public AnimPlayerBox(File file, VideoApp parent, AnimDecoder decoder,
                         boolean close_when_finished) {
        autoclose = close_when_finished;
        this.file = file;
        this.parent = parent;
//        int frameCount = decoder.getFrameCount();
//        System.out.println(frameCount);

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
            int frameNum = 0;
            ImageIcon iic = new ImageIcon();
            label.setIcon(iic);
            while (!stopFlag.get()) {
                if (!waitFlag.get()) {
                    Image im2 = null;
                    try {
                        im2 = __que.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(im2.equals(stopImage))
                        break;
                    im2 = im2.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_DEFAULT);

                    frameNum++;
                    if (saveFlag) {
                        ImgTools.writeToFile(im2, "jpg", parent.snapDir, "" + frameNum);
                    }
                    iic.setImage(im2);
                    label.repaint();
                    Tools.delay(sleepTime.get());
                }
            }
            if (autoclose)
                stop();
            decoderTask.cancel(true);
            __que.clear();
            //Tools.gc_now();
            System.out.println("end thread");
        });

        decoderTask = Tools.runTask(() -> {
            decoder.decodeFile(file.getAbsolutePath(), __que);
            try {
                __que.put (stopImage);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("all frames decoded!");
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
