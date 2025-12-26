package dialogs;

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
    private JList<String> list1;
    private JRadioButton radioAND;
    private JRadioButton radioOR;
    private JButton cancelButton;
    private JTextField searchField;
    private boolean cancelled = false;
    private boolean andMode = true;
    private MyListCellRenderer this_ml;

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
        radioAND.addActionListener(_ -> andMode = true);
        radioOR.addActionListener(_ -> andMode = false);
        pack();
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                this_ml.setMark(-1); // clear all
                String ss = searchField.getText().toLowerCase();
                if (ss.isEmpty()) {
                    list1.repaint(); // all clear?
                    return;
                }
                ListModel<String> lm = list1.getModel();
                for (int n = 0; n < lm.getSize(); n++) {
                    if (lm.getElementAt(n).contains(ss)) {
                        final int nn = n;
                        SwingUtilities.invokeLater(() -> {
                            this_ml.setMark(nn);
                            list1.repaint();
                        });
                    }
                }
            }
        });
    }

    public static JList<String> open() {
        TagSelectorDlg dialog = new TagSelectorDlg();
        //dialog.setSize(1100,400);
        dialog.setLocation(100,100); // setLocationRelativeTo(null);
        dialog.pack();
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
        TreeSet<String> tags = DBHandler.getImageTagList();
        list1 = new JList<>(tags.toArray(new String[0]));
        list1.setCellRenderer (new MyListCellRenderer());
        this_ml = (MyListCellRenderer)list1.getCellRenderer();
        list1.setVisibleRowCount(20);
        list1.setLayoutOrientation(JList.HORIZONTAL_WRAP);
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
            new TheGrid(sql.toString(), "WORKER");
        })).start();
    }
}
