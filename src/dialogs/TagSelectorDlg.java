package dialogs;

import database.DBHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelled = true;
                super.windowClosing(e);
            }
        });

        buttonOK.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> onCancel());
        radioAND.addActionListener(e -> andMode = true);
        radioOR.addActionListener(e -> andMode = false);
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

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        cancelled = true;
        dispose();
    }

    private void createUIComponents() {
        TreeSet<String> tags = DBHandler.getInst().getImageTagList();
        list1 = new JList<>(tags.toArray(new String[0]));
    }
}
