package dialogs;

import common.BlockCaret;
import common.UnrelatedPair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Input extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField textField1;
    private JTextField tf2;
    private JLabel lab2;
    private JLabel lab1;

    public Input() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        //buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static String getText (String desc, String defValue) {
        Input dialog = new Input();
        dialog.setLocation (new Point(100,100));  // MouseInfo.getPointerInfo().getLocation());
        dialog.textField1.setText(defValue);
        dialog.setTitle(desc);
        dialog.lab1.setText(desc);
        dialog.textField1.setToolTipText("Enter text here");
        dialog.tf2.setVisible(false);
        dialog.lab2.setVisible(false);
        dialog.pack();
        dialog.textField1.setCaret(new BlockCaret());
        dialog.setVisible(true);
        return dialog.textField1.getText();
    }

    public static int getInteger (String desc, String defValue) {
        Integer p = null;
        do {
            Input dialog = new Input();
            dialog.setLocation (MouseInfo.getPointerInfo().getLocation());
            dialog.textField1.setText(defValue);
            dialog.setTitle(desc);
            dialog.lab1.setText(desc);
            dialog.textField1.setToolTipText("Enter value here");
            dialog.tf2.setVisible(false);
            dialog.lab2.setVisible(false);
            dialog.pack();
            dialog.textField1.setCaret(new BlockCaret());
            dialog.setVisible(true);
            String valueText = dialog.textField1.getText();
            try {
                p = Integer.parseInt(valueText);
            } catch (NumberFormatException e) {
                dialog.dispose();
            }
        } while (p == null);
        return p;
    }

    @Contract("_ -> new")
    public static @NotNull UnrelatedPair<Integer, Integer> getIntPair(String desc) {
        UnrelatedPair<Integer, Integer> p = null;
        do {
            Input dialog = new Input();
            dialog.setTitle(desc);
            dialog.pack();
            dialog.textField1.setCaret(new BlockCaret());
            dialog.tf2.setCaret(new BlockCaret());
            dialog.setVisible(true);
            String t1 = dialog.textField1.getText();
            String t2 = dialog.tf2.getText();
            dialog.dispose();
            try {
                p = new UnrelatedPair<>(Integer.parseInt(t1), Integer.parseInt(t2));
            } catch (NumberFormatException e) {
                //p = null;
            }
        } while (p == null);
        return p;
    }

    public static void main(String[] args) {
        UnrelatedPair<Integer, Integer> p = getIntPair("hello world");
        //Integer p = getInteger("hello");
        System.out.println(p);
        System.exit(0);
    }
}
