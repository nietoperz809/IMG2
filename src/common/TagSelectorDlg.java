package common;

import database.DBHandler;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.TreeSet;

public class TagSelectorDlg extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JList<String> list1;
    private JRadioButton radioAND;
    private JRadioButton radioOR;
    private JButton cancelButton;
    private boolean cancelled = false;
    private boolean andMode = true;

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
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        radioAND.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                andMode = true;
            }
        });
        radioOR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                andMode = false;
            }
        });
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        cancelled = true;
        dispose();
    }

    public static JList<String> open() {
        TagSelectorDlg dialog = new TagSelectorDlg();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        if (dialog.cancelled)
            return null;
        dialog.list1.setOpaque(dialog.andMode);
        return dialog.list1;
    }

    private void createUIComponents() {
        TreeSet<String> tags = DBHandler.getInst().getImageTagList();
        list1 = new JList<>(tags.toArray(new String[0]));
    }
}
