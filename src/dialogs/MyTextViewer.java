package dialogs;

import database.DBHandler;

import javax.swing.*;
import java.awt.event.*;

public class MyTextViewer extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextPane textPane1;

    public MyTextViewer() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        // close dialog
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onOK(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void showDatabase() {
        MyTextViewer dialog = new MyTextViewer();
        dialog.textPane1.setContentType("text/html");
        String s = DBHandler.getDBStructure();
        s = s.replace ("\n", "<br>");
        dialog.textPane1.setText("<html><b>"+s+"</b></html>");
        dialog.setSize(800, 600);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        MyTextViewer dialog = new MyTextViewer();
        dialog.setSize(800, 600);
        dialog.setVisible(true);
        System.exit(0);
    }
}
