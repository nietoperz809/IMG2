package dialogs;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TagListCellRenderer extends JLabel implements ListCellRenderer<String> {

    private final Set<Integer> markedIndices = new HashSet<>();

    public TagListCellRenderer() {
        setOpaque(true);
    }

    public void setMark(int idx, boolean set) {
        if (idx == -1) {
            markedIndices.clear();
            return;
        }
        if (set)
            markedIndices.add(idx);
        else
            markedIndices.remove(idx);
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
        }
        if (markedIndices.contains(index)) {
            background = Color.ORANGE;
        }
        setBackground(background);
        setForeground(foreground);
        return this;
    }
}
