package dialogs;

import database.DBHandler;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.TreeSet;

import static common.Tools.*;

public class LineInput extends JDialog {
    private JPanel contentPane;
    private JTextField textField1;
    private JLabel label;
    private JButton xButton;
    private JList<String> list1;
    private JScrollPane scroller;
    private JButton addTags;
    private JButton buttonOK;
    private JPanel innerPanel;
    private JPanel upperPanel;

    private void listToText() {
        TreeSet<String> set2 = SetFromCSVString(textField1.getText());
        set2.addAll(list1.getSelectedValuesList());
        list1.clearSelection();
        set2.remove("");
        textField1.setText(CsvStringFromSet(set2));
    }

    public LineInput(Color col) {

        buttonOK.addActionListener(e -> {
            textField1.setText(adjustCSVString(textField1.getText()));
            if (!list1.getSelectedValuesList().isEmpty()) {
                listToText();
            }
            dispose();
        });

        addTags.addActionListener(e -> {
            listToText();
        });

        setContentPane(contentPane);
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        textField1.addActionListener(actionEvent -> dispose());

        xButton.addActionListener(e -> onCancel());

        setModal(true);
        setUndecorated(true);
        LineBorder border = new LineBorder(col, 4, false);
        contentPane.setBorder(border);
    }

    public static String xmain(String init, String lab, Color col) {
        return xmain(init, lab, col, null, false);
    }

    public static String xmain(String init, String lab, Color col, String tooltip, boolean hasTagList) {
        LineInput dialog = new LineInput(col);
        int len = Integer.max(600, init == null ? 100 : init.length() * 20);
        if (!hasTagList) {
            dialog.innerPanel.setVisible(false);
        }
        dialog.setSize(new Dimension(len, 50));
        dialog.textField1.setText(init);
        dialog.textField1.setToolTipText(tooltip);
        dialog.label.setText(lab);
        //dialog.pack();
        if (hasTagList) {
            dialog.setUndecorated(false);
            dialog.setSize(len + 50, 300);
            dialog.repaint();
        }
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.textField1.getText();
    }

    public static String tagList(String init, String lab, Color col) {
        String ret = xmain(init, lab, col, null, true);
        return ret;
    }

    public static int onlyPosNumber(String init, String lab, Color col) {
        String res = xmain(init, lab, col, null, false);
        try {
            int i = Integer.parseInt(res);
            if (i < 0)
                throw new RuntimeException("no neg number");
            return i;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private void onCancel() {
        textField1.setText(null);
        dispose();
    }

    private void createUIComponents() {
        TreeSet<String> tags = DBHandler.getInst().getImageTagList();
        list1 = new JList<>(tags.toArray(new String[0]));
        innerPanel = new JPanel();
    }
}
