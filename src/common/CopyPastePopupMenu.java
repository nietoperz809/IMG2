package common;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CopyPastePopupMenu extends JPopupMenu {
    public CopyPastePopupMenu (final JTextField jt) {
        jt.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseReleased(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    show(e.getComponent(), e.getX(), e.getY());
            }
        });

        Tools.addMenuItem(this , "Copy", _ -> {
                SystemClipboard.setString(jt.getText());
            });
        Tools.addMenuItem(this , "Paste", _ -> {
                jt.setText(SystemClipboard.getArray()[0]);
            });
   }
}
