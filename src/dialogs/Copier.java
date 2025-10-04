package dialogs;

import common.*;
import database.DBHandler;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import static common.MsgBox.Info;
import static common.Tools.*;
import static database.DBHandler.closeDatabase;

public class Copier extends JDialog {
    private boolean dbClosed;
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField fromText;
    private JTextField toText;
    private JTextField progressText;
    private JRadioButton rbFrom;
    private JRadioButton rbTo;
    private JButton buttonCloseDB;
    private JList<String> chunkList;
    public static final long MB10 = 1024*1024*10;
    public static final long MB50 = 1024*1024*50;
    public static final long MB100 = 1024*1024*100;


    public Copier() {
        chunkList.setSelectedIndex(1); // 10MB

        PersistString pFrom = new PersistString("pfrom", "fromFile");
        doForInit(pFrom, fromText, rbFrom);

        PersistString pTo = new PersistString("pTo", "toFile");
        doForInit(pTo, toText, rbTo);

        setTitle("Fast CopyBox");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        buttonCloseDB.addActionListener(_ -> {
            Sam.speak("Database disconnect");
            closeDatabase(true);
            dbClosed = true;
        });
    }

    private void doForRadioButton(JTextField jt, PersistString ps, boolean directory) {
        JFileChooser fc = new JFileChooser();
        if (directory) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
        }
        if (fc.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
            jt.setText(fc.getSelectedFile().getAbsolutePath());
            jt.setBackground(Color.YELLOW);
            ps.set(jt.getText());
        }
    }

    private void doForInit(PersistString ps, JTextField jt, JRadioButton rb) {
        jt.setText(ps.get());
        jt.setOpaque(true);
        jt.addActionListener(e -> {
            jt.setBackground(Color.YELLOW);
            ps.set(jt.getText());
        });
        jt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                jt.setBackground(Color.WHITE);
            }
        });
        rb.addActionListener(_ -> doForRadioButton(jt, ps, rb.equals(rbTo)));
    }

    private String elapsed(Instant startTime) {
        Duration diff = Duration.between(startTime, Instant.now());
        return String.format("%d:%02d:%02d",
                diff.toHours(),
                diff.toMinutesPart(),
                diff.toSecondsPart());
    }

    private void onOK() {
        Tools.runTask(() -> {
            try {
                long chunksize = switch (chunkList.getSelectedIndex()) {
                    case 1 -> MB50;
                    case 2 -> MB10;
                    default -> MB100;
                };
                buttonOK.setEnabled(false);
                Instant startTime = Instant.now();
                Path destP = Paths.get(toText.getText() +
                        File.separatorChar + DBHandler.DB_FILE+DBHandler.DB_EXT);
                createMisssingDirs(destP);
                Channelcopy.performCopy(Paths.get(fromText.getText()),
                        destP,
                        chunksize,
                        (transferred, size) -> {
                            long max = size / chunksize;
                            long x = transferred / chunksize;
                            progressText.setText(x + " from " + max + " blocks in "+elapsed(startTime));
                            progressText.paintImmediately(0, 0, progressText.getWidth(), progressText.getHeight());
                            contentPane.repaint();
                            return false; // true will stop the copy
                        });
                buttonOK.setEnabled(true);
                Info("DB backup took: " + elapsed(startTime));
            } catch (IOException e) {
                MsgBox.Error (e.toString());
                buttonOK.setVisible(true);
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean main(String[] ignoredArgs) {
        Copier dialog = new Copier();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.dbClosed;
    }
}
