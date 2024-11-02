package dialogs;

import javax.swing.*;
import java.awt.event.*;

public class UnlockDialog extends JDialog {
    private JPanel contentPane;
    private JPasswordField passwordField1;

    public UnlockDialog(String title) {
        setContentPane(contentPane);
        setModal(true);
        setTitle(title == null ? "Unlock ..." : title);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        passwordField1.addActionListener(e -> dispose());
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static String xmain(String title) {
        UnlockDialog dialog = new UnlockDialog(title);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.passwordField1.getText();
    }
}
