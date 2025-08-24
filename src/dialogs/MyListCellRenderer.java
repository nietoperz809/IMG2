package dialogs;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MyListCellRenderer extends JLabel implements ListCellRenderer<String>{

    public MyListCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        setText(value);
        setToolTipText(value);
        setBorder(new LineBorder(Color.BLUE));

        Color background;
        Color foreground;
        if (isSelected) {
            background = Color.RED;
            foreground = Color.WHITE;
        } else {
            background = Color.WHITE;
            foreground = Color.BLACK;
        };
        setBackground(background);
        setForeground(foreground);
        return this;
    }
}
