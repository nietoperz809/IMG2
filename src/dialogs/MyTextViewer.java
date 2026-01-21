package dialogs;

import common.SystemClipboard;
import database.DBHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static common.Tools.commatize;

public class MyTextViewer extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextPane textPane1;

    public MyTextViewer() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(_ -> onOK());

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
        String t = textPane1.getText();
        Document doc = Jsoup.parse(t);
        SystemClipboard.setString(doc.text());
        dispose();
    }

    public static void showDatabase() {
        MyTextViewer dialog = new MyTextViewer();
        String i1 = DBHandler.getRowCount("IMAGES");
        String i2 = DBHandler.getRowCount("VIDEOS");
        String i3 = DBHandler.getRowCount("GIFS");
        String i4 = DBHandler.getRowCount("WEBP");
        String root = "\n-- Root: "+DBHandler.getUrl();
        String count = "-- "+i1+" "+i2+" "+i3+" "+i4;
        dialog.textPane1.setContentType("text/html");
        String s = DBHandler.getDBStructure()+"\n"+count;
        s += root;
        s += ("\n-- Total DB size: " + commatize(Long.parseLong(DBHandler.getDBFileSize())));
        s = s.replace ("\n", "<br>");
        dialog.textPane1.setText("<html><b>"+s+"</b></html>");
        dialog.setSize(800, 600);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        MyTextViewer dialog = new MyTextViewer();
        dialog.textPane1.setText("Hello World");
        dialog.setSize(800, 600);
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        textPane1 = new JTextPane();
        Font customFont = new Font("Serif", Font.BOLD, 20);
        textPane1.setFont(customFont);
    }
}
