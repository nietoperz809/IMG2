package dialogs;

import common.Sam;
import database.DBHandler;
import thegrid.TheGrid;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;

import static database.SqlListFunctions.deleteQuery;
import static database.SqlListFunctions.getQueries;

public class SqlList {
    private JPanel panel1;
    private JButton button1;
    private JComboBox<String> combo1;

    public SqlList(JFrame host) {
        combo1.setToolTipText("<HTML>"+
                "CTRL+LeftMouse -- delete selected row<br>"+
                "Enter -- execute SQL"+
                "</HTML>");

        combo1.addActionListener(e -> {
            DBHandler.GridQuery gq = DBHandler.GridQuery.fromString((String) Objects.requireNonNull(combo1.getSelectedItem()));
            if (e.getModifiers() == 18) { // ctrl+mouse
                deleteQuery (gq.rowid());
                Sam.speak("Row removed!");
                populateList();
            } else if (e.getActionCommand().equals("comboBoxEdited")) {
                (new Thread(() -> new TheGrid(gq.sql(),"WORKER"))).start();
                host.dispose();
            }
        });
    }

    public void populateList() {
        ArrayList<DBHandler.GridQuery> al = getQueries();
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
