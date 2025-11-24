package dialogs;

import javax.swing.*;
import java.awt.event.*;
import java.util.TimerTask;

public class TimedMsg2 extends JDialog {
    private int thisSec;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel label;
    private JCheckBox defragCheckBox;
    private boolean retval;

    public TimedMsg2 (String head, int seconds) {
        thisSec = seconds;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle(head);

        // timer
        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                thisSec--;
                label.setText("shutdown in "+thisSec+"s");
                pack();
                if (thisSec == 0) {
                    onCancel();
                }
            }
        }, 1000, 1000);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

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
        retval = true;
        dispose();
    }

    private void onCancel() {
        retval = false;
        dispose();
    }

    /**
     * Show Box & start timer
     * @return bool array
     * [0] -> false == time expired
     * [1] -> defrag checkbox status
     */
    public static boolean[] doTimedBox() {
        TimedMsg2 dialog = new TimedMsg2 (
                "Shutdown the app",
                30);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        boolean[] arr = new boolean[2];
        arr[0] = dialog.retval;
        arr[1] = dialog.defragCheckBox.isSelected();
        return arr;
    }

    public static void main(String[] args) {
        System.out.println(doTimedBox());
        System.exit(0);
    }
}
