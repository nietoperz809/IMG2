package common;

import database.DBHandler;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class TagSelectorDlg extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JList<String> list1;

    public TagSelectorDlg() {
        setUndecorated(true);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        dispose();
    }

//    private void onCancel() {
//        dispose();
//    }

    public static List<String> xmain(String[] args) {
        TagSelectorDlg dialog = new TagSelectorDlg();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.list1.getSelectedValuesList();
    }

    private void createUIComponents() {
        TreeSet<String> tags = DBHandler.getInst().getImageTagList();
        list1 = new JList<>(tags.toArray(new String[0]));
    }
}
