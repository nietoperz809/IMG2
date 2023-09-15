package thegrid;

import database.DBHandler;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LogBox {
    private static JFrame frame;
    private JPanel panel1;
    private JTextArea textArea1;


    public LogBox() {
        textArea1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DBHandler.getInst().reduceLog();
                createUIComponents();
            }
        });
    }

    public static void xmain() {
        if (frame == null) {
            frame = new JFrame("LogBox");
        }
        LogBox lb = new LogBox();
        frame.setContentPane(lb.panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        textArea1 = new JTextArea();
        ArrayList<DBHandler.LogMessage> al = DBHandler.getInst().getLog();
        for (DBHandler.LogMessage lm: al) {
            textArea1.append(lm.toString());
        }
        textArea1.append("LogEntries: "+al.size());
    }
}
