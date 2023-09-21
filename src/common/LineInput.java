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

    public LineInput() {
        setContentPane(contentPane);
        setModal(true);
        //getRootPane().setDefaultButton(buttonOK);
        setUndecorated(true);
        LineBorder border = new LineBorder(Color.RED, 4, false);
        contentPane.setBorder(border);
        textField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });
    }

    public static String xmain (String init, String lab) {
        LineInput dialog = new LineInput();
        dialog.textField1.setText(init);
        dialog.label.setText(lab);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.textField1.getText();
    }
}
