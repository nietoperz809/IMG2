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
import static common.Tools.MB10;
import static common.Tools.MB100;
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
    final JFileChooser fc = new JFileChooser();

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
            closeDatabase();
            dbClosed = true;
        });
    }

    private void doForRadioButton(JTextField jt, PersistString ps) {
        int returnVal = fc.showOpenDialog(contentPane);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
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
        rb.addActionListener(_ -> doForRadioButton(jt, ps));
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
                long chunksize = chunkList.getSelectedIndex() == 0 ? MB100 : MB10;
                buttonOK.setVisible(false);
                Instant startTime = Instant.now();
                Path destP = Paths.get(toText.getText() +
                        File.separatorChar + DBHandler.DB_FILE+DBHandler.DB_EXT);
                Files.createDirectories(destP.getParent());
                Channelcopy.performCopy(Paths.get(fromText.getText()),
                        destP,
                        chunksize,
                        (transferred, size) -> {
                            long max = size / chunksize;
                            long x = transferred / chunksize;
                            progressText.setText(x + " from " + max + " blocks in "+elapsed(startTime));
                            repaint();
                            return false; // true will stop the copy
                        });
                buttonOK.setVisible(true);
                Info("DB backup took: " + elapsed(startTime));
            } catch (IOException e) {
                MsgBox.Error (e.toString());
                buttonOK.setVisible(true);
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean main(String[] args) {
        Copier dialog = new Copier();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.dbClosed;
    }
}
