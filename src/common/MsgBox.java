package common;

import dialogs.UnlockDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;

import static common.Tools.runTask;

public class MsgBox {
    public static void Error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private static void msgbox(Point pos, String text) {
        SwingUtilities.invokeLater(() -> {
            JDialog dlg = new JDialog((Frame) null, "Message");
            dlg.setModal(false);
            dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            JLabel lbl = new JLabel("<html><h1>&nbsp;" +
                    text.replace("\n", "<br>") +
                    "&nbsp;</h1></html>");
            dlg.getContentPane().add(lbl);
            dlg.pack();
            dlg.setLocation(pos);
            dlg.setVisible(true);
        });
    }

//    public static void Info(String msg) {
//        JFrame parentFrame = new JFrame();
//        parentFrame.setLocation(new Point (100, 100));
//        parentFrame.setUndecorated(true);
//        parentFrame.setVisible(true);
//        JOptionPane.showMessageDialog(parentFrame, msg, "Info",
//                JOptionPane.PLAIN_MESSAGE);
//        parentFrame.dispose();
//    }

    public static void AsyncInfo (final String msg) {
        runTask(() -> msgbox(new Point(100,100), msg));
    }

    public static boolean Question(String msg) {
        Object response = JOptionPane.showInputDialog(null,
                msg,
                "Please select", JOptionPane.QUESTION_MESSAGE,
                null, new String[]{"No", "Yes"}, "No");
        if (response == null)
            return false;
        return response.equals("Yes");
    }

    public static String getInput(String msg) {
        Object response = JOptionPane.showInputDialog(null,
                msg,
                "Please select", JOptionPane.QUESTION_MESSAGE,
                null, null, "");
        return (String) response;
    }

    public static String chooseDir(Component parent) {
        JFileChooser f = new JFileChooser();
        PersistString ps = new PersistString("Common.lastDirectory", System.getProperty("user.home"));
        String lastDirectory = ps.get();
        f.setSelectedFile(new File(lastDirectory));
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (f.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String dir = f.getSelectedFile().getAbsolutePath();
            return ps.set(dir);
        }
        return null;
    }

    public static void AskforPWD() throws RuntimeException {
        byte[] bt = UnlockDialog.xmain("PWD?").getBytes(Charset.defaultCharset());
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theMD5digest = md.digest(bt);
            byte[] shouldBe = {46, -123, 13, -68, 98, 92, -32, -104, -55, 20, 57, 79, -73, 120, 116, 50};
            if (!Arrays.equals(theMD5digest,shouldBe)) {
                Sam.speak(".Access denied!");
                Thread.sleep(3000);
                System.exit(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
