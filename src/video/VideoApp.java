package video;

import database.DBHandler;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.util.List;

public class VideoApp extends JDialog {
    private JPanel contentPane;
    private JButton buttonPlay;
    private JButton buttonCancel;
    private JList<String> listControl;

    public VideoApp() {
        setContentPane(contentPane);
        //setModal(true);
        getRootPane().setDefaultButton(buttonPlay);

        buttonPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

//        // call onCancel() on ESCAPE
//        contentPane.registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        List<DBHandler.NameID> list = DBHandler.getInst().getVideoFileNames();
        DefaultListModel<String> model = new DefaultListModel<>();
        for (DBHandler.NameID nid : list) {
            model.addElement(nid.name);
        }
        listControl.setModel(model);
    }

    private JFrame playerFrame;

    private void onOK() {
        if (playerFrame != null)
            return;
        String name = (String) listControl.getSelectedValue();
        try {
            Path temp = DBHandler.getInst().loadVideo(name);
            System.out.println(temp);
            playerFrame = new JFrame("My First Media Player");
            playerFrame.setBounds(100, 100, 600, 400);
            playerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            playerFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    onCancel();
                }
            });
            EmbeddedMediaPlayerComponent mpc
                    = new EmbeddedMediaPlayerComponent();
            playerFrame.setContentPane(mpc);
            playerFrame.setVisible(true);
            mpc.mediaPlayer().videoSurface().attachVideoSurface();
            mpc.mediaPlayer().media().play(temp.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onCancel() {
        if (playerFrame == null)
            return;
        EmbeddedMediaPlayerComponent mpc = (EmbeddedMediaPlayerComponent) playerFrame.getContentPane();
        mpc.release();
        //mpc.mediaPlayer().media().
        playerFrame.dispose();
        playerFrame = null;
    }

    public static void main(String[] args) {
        VideoApp dialog = new VideoApp();
        dialog.setSize(400,400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
