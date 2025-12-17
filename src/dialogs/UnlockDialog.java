package dialogs;

import common.Tools;
import common.Win32;

import javax.swing.*;
import java.awt.event.*;

public class UnlockDialog extends JDialog {
    private JPanel contentPane;
    private JPasswordField passwordField1;
    private JCheckBox checkBox1;
    private JButton OKButton;
    private static final char EC = '╳';

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
        contentPane.registerKeyboardAction(_ -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        passwordField1.addActionListener(_ -> dispose());

        passwordField1.setEchoChar(EC);
        checkBox1.addItemListener(e ->
                passwordField1.setEchoChar (e.getStateChange() == ItemEvent.SELECTED ? (char)0 : EC));
        OKButton.addActionListener(_ -> dispose());
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static String xmain(String title) {
        final UnlockDialog dialog = new UnlockDialog(title);
        dialog.setBounds(0,0,500,100);
        dialog.setLocationRelativeTo(null);
        Tools.runTask(() -> {
            while (!dialog.isVisible()) {
                Tools.delay(100);
            }
            Win32.dialogToTop(dialog);
            System.out.println("toTop");
        });
        dialog.setVisible(true);
        return new String(dialog.passwordField1.getPassword());
    }

    public static void main(String[] args) {
        String p = xmain ("hello");
        System.out.println(p);
    }
}
