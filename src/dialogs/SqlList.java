package dialogs;

import database.DBHandler;
import thegrid.TheGrid;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class SqlList {
    private JPanel panel1;
    private JButton button1;
    private JComboBox<String> combo1;

    public SqlList(JFrame host) {
        combo1.setToolTipText("CTRL+LeftMouse to delete selected row");

        combo1.addActionListener(e -> {
            if (e.getModifiers() == 18) {
                DBHandler.deleteQuery (combo1.getSelectedItem());
                populateList();
                return;
            }
            if (e.getActionCommand().equals("comboBoxEdited")) {
                (new Thread(() -> new TheGrid((String)combo1.getSelectedItem(),
                        "WORKER"))).start();
                host.dispose();
            }
        });
    }

    public void populateList() {
        ArrayList<String> al = DBHandler.getQueries();
        DefaultComboBoxModel<String> lm = new DefaultComboBoxModel<>();
        for (String s : al) {
            lm.addElement(s);
        }
        combo1.setModel(lm);
    }

    public static void xmain() {
        JFrame frame = new JFrame("SqlList");
        SqlList sqll = new SqlList(frame);
        sqll.populateList();
        frame.setContentPane(sqll.panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
