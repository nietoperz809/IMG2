package common;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CopyPastePopupMenu extends JPopupMenu {
        public CopyPastePopupMenu(JTextField jt) {
            Tools.addMenuItem(this , "Copy", _ -> {
                SystemClipboard.setString(jt.getText());
            });
            Tools.addMenuItem(this , "Paste", _ -> {
                jt.setText(SystemClipboard.getArray()[0]);
            });
        }

    public static class PopClickListener extends MouseAdapter {
        private final JTextField jt;
        public PopClickListener(JTextField jt) {
            this.jt = jt;
        }

        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger())
                doPop(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger())
                doPop(e);
        }

        private void doPop(MouseEvent e) {
            CopyPastePopupMenu menu = new CopyPastePopupMenu(jt);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
