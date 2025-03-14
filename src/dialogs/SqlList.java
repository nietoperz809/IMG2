package dialogs;

import javax.swing.*;

public class SqlList {
    private JPanel panel1;
    private JButton button1;
    private JList list1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("SqlList");
        frame.setContentPane(new SqlList().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
