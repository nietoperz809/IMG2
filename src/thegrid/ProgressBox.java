package thegrid;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ProgressBox extends JDialog {
    private final JProgressBar progressBar;

    public ProgressBox(Frame owner, int maxlen) {
        this (owner, maxlen,Math.min(maxlen, 500));
    }

    public ProgressBox(Frame owner, int maxlen, int boxlen) {
        super(owner);
        progressBar = new JProgressBar(0, maxlen);
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.BLACK);
        LineBorder border = new LineBorder(Color.RED, 4, false);
        ((JPanel)this.getContentPane()).setBorder(border);
        setLayout(new BorderLayout());
        add (new JLabel ("Please wait: "), BorderLayout.WEST);
        add(progressBar, BorderLayout.CENTER);
        setUndecorated(true);
        setSize(boxlen,30);
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    public void setTextAndValue(String txt, int v) {
        progressBar.setValue(v);
        progressBar.setString(txt);
        repaint();
    }
}
