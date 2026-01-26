package video;

import common.Pair;
import common.Tools;
import common.UpDown;
import database.DBHandler;
import dialogs.video.ConBrightV;
import uk.co.caprica.vlcj.player.base.ControlsApi;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static database.VideoFunctions.getVideoAsFile;
import static video.VideoType.pVideo;

public class MP4PlayerBox implements PlayerBox {
    private static final Lock lock = new ReentrantLock();
    private final JScrollBar sbar;
    private final DBHandler.NameID nid;
    private final boolean autoclose;
    private volatile JFrame playerFrame;
    private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private boolean paused = false;
    private final VideoApp parent;
    private UpDown speed;

    public MP4PlayerBox(VideoApp parent, DBHandler.NameID nid, boolean autoclose) {
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
            if (mediaPlayerComponent == null)
                return;
            if (adjustmentEvent.getValueIsAdjusting()) {
                ControlsApi mp = mediaPlayerComponent.mediaPlayer().controls();
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
                if (mediaPlayerComponent != null && SwingUtilities.isRightMouseButton(mouseEvent)) {
                    var mp = mediaPlayerComponent.mediaPlayer().controls();
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
            File tempFile = getVideoAsFile (nid, pVideo);
            mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
            MediaPlayer mp = mediaPlayerComponent.mediaPlayer();
            //mediaPlayerComponent.mediaPlayer().fullScreen().set (true);
            //mp.video().setAdjustVideo(true);
            //mp.inter
            playerFrame = new JFrame();
            playerFrame.requestFocus();
            //playerFrame.setTitle("Hit 's' to start and stop, 'p' to take snapshot, +/- for speed, 'b' to show sliders");
            playerFrame.setUndecorated(true);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            playerFrame.setBounds(0, 0, screenSize.width, screenSize.height-30);
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
             * b -- show sliders
             */
            playerFrame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    char c = keyEvent.getKeyChar();
                    var controls = mediaPlayerComponent.mediaPlayer().controls();
                    switch (c) {
                        case '\u001B' -> SwingUtilities.invokeLater(() -> stop()); // Escape key
                        case 'b' -> {
                            MP4PlayerBox.this.parent.setVisible(false);
                            ConBrightV.popup(mediaPlayerComponent.mediaPlayer().video());
                            MP4PlayerBox.this.parent.setVisible(true);
                        }
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
                        case 'r' -> controls.skipTime(-1000);
                        case 'f' -> controls.skipTime(1000);
                        case '+' -> {
                            controls.setRate(speed.up());
                            playerFrame.setTitle (Float.toString(speed.current()));
                        }
                        case '-' -> {
                            controls.setRate(speed.down());
                            playerFrame.setTitle (Float.toString(speed.current()));
                        }
                        case 'p' -> mediaPlayerComponent.mediaPlayer().snapshots()
                                .save(new File(parent.snapDir + File.separator + System.currentTimeMillis() + ".png"));
                        default -> throw new IllegalStateException("Unexpected value: " + c);
                    }
                }
            });
            playerFrame.setLayout(new BorderLayout());
            playerFrame.add(mediaPlayerComponent, BorderLayout.CENTER); //setContentPane(mpc);
            playerFrame.add(sbar, BorderLayout.NORTH);
            playerFrame.setVisible(true);
            /*
             * update scrollbar
             */
            mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                @Override
                public void positionChanged(MediaPlayer mediaPlayer, float v) {
                    sbar.setValue((int) (v * 1000));
                }
            });

            /*
             * video finished
             */
            mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                private void restart() {
                    mediaPlayerComponent.mediaPlayer().media().play(tempFile.getAbsolutePath());
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
            mediaPlayerComponent.mediaPlayer().videoSurface().attachVideoSurface();
            mediaPlayerComponent.mediaPlayer().media().play(tempFile.getAbsolutePath());
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
        mediaPlayerComponent.release();
        playerFrame.dispose();
        playerFrame = null;
        Tools.gc_now();
        lock.unlock();
    }

}
