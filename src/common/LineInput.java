package common;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LineInput extends JDialog {
    private JPanel contentPane;
    private JTextField textField1;
    private JLabel label;
    private JButton xButton;

    public LineInput (Color col) {
        setContentPane(contentPane);
        setModal(true);
        setUndecorated(true);
        LineBorder border = new LineBorder(col,4,false);
        contentPane.setBorder(border);
        textField1.addActionListener(actionEvent -> dispose());
        xButton.addActionListener(e -> {
            textField1.setText(null);
            dispose();
        });
    }

    public static String xmain (String init, String lab, Color col) {
        return xmain (init, lab, col, null);
    }

    public static String xmain (String init, String lab, Color col, String tooltip) {
        LineInput dialog = new LineInput(col);
        int len = Integer.max (600, init == null ? 100 : init.length()*20);

        dialog./*textField1.*/setPreferredSize(
                new Dimension(len, 50)
        );
        dialog.textField1.setText(init);
        dialog.textField1.setToolTipText(tooltip);
        dialog.label.setText(lab);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.textField1.getText();
    }

    public static int onlyPosNumber (String init, String lab, Color col) {
        String res = xmain (init, lab, col, null);
        try {
            int i = Integer.parseInt(res);
            if (i < 0)
                throw new RuntimeException("no neg number");
            return i;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }


}
