package dialogs;

import common.CopyPastePopupMenu;
import common.Csv;
import database.DBHandler;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.TreeSet;

public class Tagger extends JDialog {
    private JPanel contentPane;
    private JTextField textField1;
    private JLabel label;
    private JButton xButton;
    private JList<String> list1;
    private JButton buttonOK;
    private JPanel innerPanel;
    private JTextField searchField;
    private String initText;
    private MyListCellRenderer this_ml;

    private void listToText() {
        TreeSet<String> set2 = Csv.getSetFromCSVString(textField1.getText());
        Csv.combineSpecial(set2, list1.getSelectedValuesList());
        textField1.setText(Csv.CsvStringFromSet(set2));
        System.out.println(textField1.getText());
    }

    public Tagger(Color col) {
        list1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        buttonOK.addActionListener(e -> {
            if (innerPanel.isVisible()) {
                String str = Csv.normalizeCSVString(textField1.getText());
                textField1.setText(str);
            }
            dispose();
        });

        list1.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                listToText();
            } else {
                list1.clearSelection();
            }
        });

        setContentPane(contentPane);
        contentPane.registerKeyboardAction(_ -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        textField1.addActionListener(_ -> dispose());

        xButton.addActionListener(_ -> onCancel());

        setModal(true);
        setUndecorated(true);
        LineBorder border = new LineBorder(col, 4, false);
        contentPane.setBorder(border);

        // Handle searchbox actions
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
                        //System.out.println(lm.getElementAt(n));
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

    public static String xmain(String init, String lab, Color col) {
        return xmain(init, lab, col, false);
    }

    private static String xmain(String init, String lab, Color col, boolean hasTagList) {
        Tagger dialog = new Tagger(col);
        int len = Integer.max(600, init == null ? 100 : init.length() * 20);
        if (!hasTagList) {
            dialog.innerPanel.setVisible(false);
        }
        dialog.setSize(new Dimension(len, 50));
        dialog.initText = init;
        dialog.textField1.setText(init);
        //dialog.textField1.setToolTipText(null);
        new CopyPastePopupMenu(dialog.textField1); // create popup menu
        dialog.label.setText(lab);
        if (hasTagList) {
            dialog.setUndecorated(false);
            dialog.pack();
        }
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.textField1.getText();
    }

    public static String tagList(String init, String lab, Color col) {
        return xmain(init, lab, col, true).trim().toLowerCase();
    }

    private void onCancel() {
        if (label.getText().equals("newSQL")) {
            textField1.setText("");
        } else {
            textField1.setText(initText); // restore initial tag list
        }
        dispose();
    }

    private void createUIComponents() {
        TreeSet<String> tags = DBHandler.getImageTagList();
        list1 = new JList<>(tags.toArray(new String[0]));
        list1.setCellRenderer(new MyListCellRenderer());
        this_ml = (MyListCellRenderer)list1.getCellRenderer();
    }
}
