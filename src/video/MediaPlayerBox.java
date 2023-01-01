package video;

import common.Tools;
import database.DBHandler;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MediaPlayerBox {
    private static final Lock lock = new ReentrantLock();
    private final JScrollBar sbar;
    private volatile JFrame playerFrame;
    private EmbeddedMediaPlayerComponent mpc;
    private boolean paused = false;
    private VideoApp parent;


    public MediaPlayerBox (VideoApp parent) {
        this.parent = parent;
        sbar = new JScrollBar(Adjustable.HORIZONTAL);
        sbar.setBackground(Color.YELLOW);
        sbar.setMaximum(1000);
        /*
         * forward/backward by scrollbar move
         */
        sbar.addAdjustmentListener(adjustmentEvent -> {
            if (mpc == null)
                return;
            if (adjustmentEvent.getValueIsAdjusting()) {
                var mp = mpc.mediaPlayer().controls();
                lock.lock();
                //mp.pause();
                mp.setPosition(adjustmentEvent.getValue() / 1000f);
                //mp.play();
                lock.unlock();
            }
        });
        /*
         * start/stop by right click on scrollbar
         */
        sbar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mpc != null && SwingUtilities.isRightMouseButton(mouseEvent)) {
                    var mp = mpc.mediaPlayer().controls();
                    lock.lock();
                    mp.pause();
                    lock.unlock();
                }
            }
        });
    }

    public void start(String name) {
        if (playerFrame != null)
            return;
        lock.lock();
        try {
            sbar.setValue(0);
            String tempFile = DBHandler.getInst().transferVideoIntoFile(name);
            System.out.println(tempFile);
            mpc = new EmbeddedMediaPlayerComponent();
            playerFrame = new JFrame();
            playerFrame.requestFocus();
            playerFrame.setTitle("Player Box, hit 's' to start and stop, 'p' to take shapshot");
            playerFrame.setBounds(100, 100, 600, 400);
            playerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            /*
             * cleanup on window close
             */
            playerFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    stop();
                }
            });
            /*
             * Keep focus on playerframe
             */
            playerFrame.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if (playerFrame != null) {
                        playerFrame.requestFocus();
                    }
                }
            });
            /*
             * Start/Stop using 's'
             * Snapshot using 'p'
             */
            playerFrame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    char c = keyEvent.getKeyChar();
                    if (c == 's') {
                        var mp = mpc.mediaPlayer().controls();
                        lock.lock();
                        if (paused)
                            mp.play();
                        else
                            mp.pause();
                        paused = !paused;
                        lock.unlock();
                    } else if (c == 'p') {
                        mpc.mediaPlayer().snapshots()
                                .save(new File(parent.snapDir + File.separator + System.currentTimeMillis() + ".png"));
                    }
                    //lock.unlock();
                }
            });
            playerFrame.setLayout(new BorderLayout());
            playerFrame.add(mpc, BorderLayout.CENTER); //setContentPane(mpc);
            playerFrame.add(sbar, BorderLayout.SOUTH);
            playerFrame.setVisible(true);
            /*
             * update scrollbar
             */
            mpc.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                @Override
                public void positionChanged(MediaPlayer mediaPlayer, float v) {
                    sbar.setValue((int) (v * 1000));
                }
            });
            mpc.mediaPlayer().videoSurface().attachVideoSurface();
            mpc.mediaPlayer().media().play(tempFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        if (playerFrame == null)
            return;
        lock.lock();
        System.out.println("stop");
        mpc.release();
        playerFrame.dispose();
        playerFrame = null;
        Tools.gc();
        lock.unlock();
    }

}
