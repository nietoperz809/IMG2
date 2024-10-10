package dialogs;

import common.MemoryMonitor;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.awt.BorderLayout.CENTER;

public class MonitorFrame extends javax.swing.JDialog {

    private MemoryMonitor memo = new MemoryMonitor();


    public MonitorFrame() {
        setTitle("Memory Monitor");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                //System.out.println("closing");
                memo.surf.stop();
                setVisible(false);
                dispose();
            }
        });

        setLayout(new BorderLayout());
        setSize (800, 800);
        add (memo, CENTER);
        memo.surf.start();
        setVisible(true);
    }

//    public static void main(String[] args) {
//        new MonitorFrame();
//    }

}


