package common;

import database.DBHandler;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.TreeSet;

public class LineInput extends JDialog {
    private JPanel contentPane;
    private JTextField textField1;
    private JLabel label;
    private JButton xButton;
    private JList list1;
    private JScrollPane scroller;
    private JButton addTags;
    private JButton buttonOK;
    private JPanel innerPanel;
    private JPanel upperPanel;

    public LineInput(Color col) {

        buttonOK.addActionListener(e -> {
            dispose();
        });


        addTags.addActionListener(e -> {
            List<String> ll = list1.getSelectedValuesList();
            String s2 = Tools.CsvFromList(ll);
            String s1 = textField1.getText();
            if (!s1.isEmpty())
                s2 = s1 + ", " + s2;
            textField1.setText(s2);
        });

        setContentPane(contentPane);
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setModal(true);
        setUndecorated(true);
        LineBorder border = new LineBorder(col, 4, false);
        contentPane.setBorder(border);
        textField1.addActionListener(actionEvent -> dispose());
        xButton.addActionListener(e -> onCancel());
    }

    public static String xmain(String init, String lab, Color col) {
        return xmain(init, lab, col, null, false);
    }

    public static String xmain(String init, String lab, Color col, String tooltip, boolean list) {
        LineInput dialog = new LineInput(col);
        int len = Integer.max(600, init == null ? 100 : init.length() * 20);
        if (!list) {
            dialog.innerPanel.setVisible(false);
        }
        dialog.setSize(
                new Dimension(len, 50)
        );
        dialog.textField1.setText(init);
        dialog.textField1.setToolTipText(tooltip);
        dialog.label.setText(lab);
        //dialog.pack();
        if (list)
            dialog.setSize(len + 50, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.textField1.getText();
    }

    public static String lmain(String init, String lab, Color col) {
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
