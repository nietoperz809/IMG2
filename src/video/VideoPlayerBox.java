package video;

import common.Tools;
import common.UpDown;
import database.DBHandler;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VideoPlayerBox implements PlayerBox {
    private static final Lock lock = new ReentrantLock();
    private final JScrollBar sbar;
    private final DBHandler.NameID nid;
    private final boolean autoclose;
    private volatile JFrame playerFrame;
    private EmbeddedMediaPlayerComponent mpc;
    private boolean paused = false;
    private final VideoApp parent;
    private UpDown speed;

    public VideoPlayerBox (VideoApp parent, DBHandler.NameID nid, boolean autoclose) {
        this.autoclose = autoclose;
        this.nid = nid;
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

    public void start () {
        if (playerFrame != null)
            return;
        lock.lock();
        try {
            speed = new UpDown(new float[]{0.01f, 0.1f, 0.3f, 1.0f, 2.0f, 3.0f, 5.0f}, 3);
            sbar.setValue(0);
            File tempFile = Objects.requireNonNull(DBHandler.getInst()).transferVideoIntoFile(nid);
            System.out.println(tempFile);
            mpc = new EmbeddedMediaPlayerComponent();
            playerFrame = new JFrame();
            playerFrame.requestFocus();
            playerFrame.setTitle("Hit 's' to start and stop, 'p' to take shapshot, +/- for speed");
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            playerFrame.setBounds(0, 0, screenSize.width-20, screenSize.height-20);
            playerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            /*
             * cleanup on window close
             */
            playerFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    stop();
                }
                @Override
                public void windowClosed(WindowEvent e) {
                    parent.clientDisposed();
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
                    var controls = mpc.mediaPlayer().controls();
                    switch (c) {
                        case 's' -> {
                            lock.lock();
                            if (paused) {
                                controls.setRate(speed.current());
                                controls.play();
                            }
                            else
                                controls.pause();
                            paused = !paused;
                            lock.unlock();
                        }
                        case 'r' -> {
                            controls.skipTime(-1000);
                        }
                        case 'f' -> {
                            controls.skipTime(1000);
                        }
                        case '+' -> {
                            controls.setRate(speed.up());
                            playerFrame.setTitle (Float.toString(speed.current()));
                        }
                        case '-' -> {
                            controls.setRate(speed.down());
                            playerFrame.setTitle (Float.toString(speed.current()));
                        }
                        case 'p' -> mpc.mediaPlayer().snapshots()
                                .save(new File(parent.snapDir + File.separator + System.currentTimeMillis() + ".png"));
                    }
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
            /*
             * videao finished
             */
            mpc.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                private void restart() {
                    mpc.mediaPlayer().media().play(tempFile.getAbsolutePath());
                }

                @Override
                public void finished(MediaPlayer mediaPlayer) {
                    super.finished(mediaPlayer);
                    if (autoclose) {
                        SwingUtilities.invokeLater(() -> stop());
                    } else {
                        SwingUtilities.invokeLater(this::restart); /* Restart */
                    }
                }
            });
            mpc.mediaPlayer().videoSurface().attachVideoSurface();
            mpc.mediaPlayer().media().play(tempFile.getAbsolutePath());
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
        Tools.gc_now();
        lock.unlock();
    }

}
