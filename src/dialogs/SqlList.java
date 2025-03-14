package dialogs;

import database.DBHandler;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;

public class SqlList {
    private JPanel panel1;
    private JButton button1;
    private JList<String> list1;

    public void populateList() {
        ArrayList<String> al = DBHandler.getQueries();
        DefaultListModel<String> lm = new DefaultListModel<>();
        for (String s : al) {
            lm.addElement(s);
        }
        list1.setModel(lm);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SqlList");
        SqlList sqll = new SqlList();
        sqll.populateList();
        frame.setContentPane(sqll.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setVisible(true);
    }

}
