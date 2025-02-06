package dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimedMessage implements ActionListener
{
    private JDialog dialog;

    private TimedMessage(JDialog dialog)
    {
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        dialog.dispose();
    }

    public static JDialog showMessageDialog(Component parentComponent,
                                         String message, String title,
                                         int delayInMilliseconds)
    {
        final JOptionPane optionPane = new JOptionPane(message);
        final JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setTitle(title);
        Timer timer = new Timer(delayInMilliseconds, new TimedMessage(dialog));
        timer.setRepeats(false);
        timer.start();
        if (dialog.isDisplayable())
        {
            dialog.setVisible(true);
        }
        return dialog;
    }
}