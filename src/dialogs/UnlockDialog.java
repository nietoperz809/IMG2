package dialogs;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import common.Tools;

import javax.swing.*;
import java.awt.event.*;

import static com.sun.jna.platform.win32.WinUser.*;

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
        Tools.dialogToTop(dialog);
        dialog.setVisible(true);
        return dialog.passwordField1.getText();
    }

//    public static interface User32 extends StdCallLibrary
//    {
//
//        final User32 instance = (User32) Native.loadLibrary ("user32", User32.class);
//        WinDef.HWND FindWindowA(String className, String windowName);
//        boolean BringWindowToTop(WinDef.HWND hw);
//    }
//
//    public static void main(String[] args) {
//        xmain ("hello");
//    }
}
