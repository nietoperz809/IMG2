package dialogs;

import common.Sam;
import database.DBHandler;
import thegrid.TheGrid;

import javax.swing.*;
import java.util.ArrayList;

public class SqlList {
    private JPanel panel1;
    private JButton button1;
    private JComboBox<String> combo1;

    public SqlList(JFrame host) {
        combo1.setToolTipText("CTRL+LeftMouse to delete selected row");

        combo1.addActionListener(e -> {
            String s = (String)combo1.getSelectedItem();
            String[] parts = s.split("--");
            if (e.getModifiers() == 18) { // ctrl+mouse
                DBHandler.deleteQuery (Integer.parseInt(parts[0]));
                Sam.speak("Row removed!");
                populateList();
            } else if (e.getActionCommand().equals("comboBoxEdited")) {
                (new Thread(() -> new TheGrid(parts[1],"WORKER"))).start();
                host.dispose();
            }
        });
    }

    public void populateList() {
        ArrayList<DBHandler.GridQuery> al = DBHandler.getQueries();
        DefaultComboBoxModel<String> lm = new DefaultComboBoxModel<>();
        for (DBHandler.GridQuery s : al) {
            lm.addElement(s.toString());
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
