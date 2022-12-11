package video;

import database.DBHandler;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MediaPlayerBox {
    private volatile JFrame playerFrame;
    private EmbeddedMediaPlayerComponent mpc;
    private final JScrollBar sbar;

    public MediaPlayerBox() {
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
                mp.pause();
                mp.setPosition(adjustmentEvent.getValue()/1000f);
                mp.play();
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
                    mp.pause();
                }
            }
        });
    }

    public void start(String name) {
        if (playerFrame != null)
            return;
        try {
            String tempFile = DBHandler.getInst().transferVideoIntoFile(name);
            System.out.println(tempFile);
            mpc = new EmbeddedMediaPlayerComponent();
            playerFrame = new JFrame();
            playerFrame.requestFocus();
            playerFrame.setTitle("Video Player Box, hit s-key to start and stop the vid");
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
             */
            playerFrame.addKeyListener(new KeyAdapter() {
                static boolean stopped = false;
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    System.out.println(stopped);
                    if (keyEvent.getKeyChar() == 's') {
                        System.out.println("s pressed");
                        var mp = mpc.mediaPlayer().controls();
                        stopped = !stopped;
                        if (stopped)
                            mp.pause();
                        else
                            mp.play();
                    }
                }
            });
            playerFrame.setLayout(new BorderLayout());
            playerFrame.add (mpc, BorderLayout.CENTER); //setContentPane(mpc);
            playerFrame.add (sbar, BorderLayout.SOUTH);
            playerFrame.setVisible(true);
            /*
             * update scrollbar
             */
            mpc.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                @Override
                public void positionChanged(MediaPlayer mediaPlayer, float v) {
                    sbar.setValue((int)(v*1000));
                }
            });
            mpc.mediaPlayer().videoSurface().attachVideoSurface();
            mpc.mediaPlayer().media().play(tempFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (playerFrame == null)
            return;
        System.out.println("stop");
        mpc.release();
        playerFrame.dispose();
        playerFrame = null;
    }

}
