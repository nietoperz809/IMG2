package thegrid.gridmenu;

import javax.swing.*;
import java.awt.*;

public class ColoredMenuItem extends JMenuItem {

    public ColoredMenuItem(String label, Color col) {
        this (label, col, Color.LIGHT_GRAY);
    }

    public ColoredMenuItem(String label, Color col, Color back) {
        super (label);
        setOpaque(true);
        setBackground(back);
        setForeground(col);
    }
}
