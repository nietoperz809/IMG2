package dialogs;

import common.Channelcopy;
import common.NumToText;
import common.PersistString;
import common.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Paths;

import static common.Tools.MB100;

public class Copier extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField fromText;
    private JTextField toText;
    private JTextField progressText;
    private JRadioButton rbFrom;
    private JRadioButton rbTo;
    private final PersistString pFrom;
    private final PersistString pTo;
    final JFileChooser fc = new JFileChooser();

    public Copier() {
        pFrom = new PersistString("pfrom", "fromFile");
        fromText.setText(pFrom.get());
        fromText.setOpaque(true);
        fromText.addActionListener(e -> {
            fromText.setBackground(Color.YELLOW);
            pFrom.set(fromText.getText());
        });
        fromText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                fromText.setBackground(Color.WHITE);
            }
        });

        pTo = new PersistString("pTo", "toFile");
        toText.setText(pTo.get());
        toText.setOpaque(true);
        toText.addActionListener(e -> {
            toText.setBackground(Color.YELLOW);
            pTo.set(toText.getText());
        });
        toText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                toText.setBackground(Color.WHITE);
            }
        });

        setTitle("CopyBox");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        rbFrom.addActionListener(_ -> doForRadioButton(fromText, pFrom));
        rbTo.addActionListener(_ -> doForRadioButton(toText, pTo));
    }

    private void doForRadioButton(JTextField jt, PersistString ps) {
        int returnVal = fc.showOpenDialog(contentPane);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            jt.setText(fc.getSelectedFile().getAbsolutePath());
            jt.setBackground(Color.YELLOW);
            ps.set(jt.getText());
        }
    }

    private void onOK() {
        Tools.runTask(() -> {
            try {
                buttonOK.setVisible(false);
                Channelcopy.performCopy(Paths.get(fromText.getText()),
                        Paths.get(toText.getText()),
                        MB100,
                        (transferred, size) -> {
                            progressText.setText(NumToText.convert(transferred/MB100));
                            repaint();
                            return false; // true will stop the copy
                        });
            } catch (IOException e) {
                buttonOK.setVisible(true);
                throw new RuntimeException(e);
            }
        });
    }

    public static void main(String[] args) {
        Copier dialog = new Copier();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        System.exit(0);
    }
}
