package dialogs;

import common.MsgBox;
import common.Tools;
import database.DBHandler;
import thegrid.TheGrid;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TreeSet;

public class TagSelectorDlg extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JList<String> tagList;
    private JRadioButton radioAND;
    private JRadioButton radioOR;
    private JButton cancelButton;
    private JTextField searchField;
    private boolean cancelled = false;
    private boolean logic = true;

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

        buttonOK.addActionListener(_ -> onOK());
        cancelButton.addActionListener(_ -> onCancel());
        radioAND.addActionListener(_ -> logic = true);
        radioOR.addActionListener(_ -> logic = false);
        searchField.setToolTipText("type (partial) tag name then select found tags to construct SQL, finally hit 'submit'");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                Tools.implementSearchboxAction(searchField, tagList);
            }
        });
        pack();
    }

    public static JList<String> open() {
        TagSelectorDlg dialog = new TagSelectorDlg();
        //dialog.setSize(1100,400);
        dialog.setLocation(100,100); // setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
        if (dialog.cancelled)
            return null;
        dialog.tagList.setOpaque(dialog.logic);
        return dialog.tagList;
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        cancelled = true;
        dispose();
    }

    private void createUIComponents() {
        TreeSet<String> tags = DBHandler.getImageTagList();
        tagList = new JList<>(tags.toArray(new String[0]));
        tagList.setCellRenderer (new TagListCellRenderer());
        //this_ml = (MyListCellRenderer)list1.getCellRenderer();
        tagList.setVisibleRowCount(20);
        tagList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    }

    public static void worker_for_tagList() {
        JList<String> jlist = open();
        if (jlist == null) // cancelled
            return;
        var list = jlist.getSelectedValuesList();
        boolean andMode = jlist.isOpaque();
        (new Thread(() -> {
            StringBuilder sql = new StringBuilder("select name,_ROWID_,tag,accnum from IMAGES where");
            for (int s = 0; s < list.size(); s++) {
                if (s > 0)
                    sql.append(andMode ? " and" : " or");
                sql.append(" tag like " + "'%").append(list.get(s)).append("%'");
            }
            System.out.println(sql);
            MsgBox.AsyncInfo(sql.toString());
            new TheGrid(sql.toString(), "WORKER");
        })).start();
    }
}
