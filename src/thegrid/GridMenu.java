package thegrid;

import common.PersistString;
import common.Sam;
import common.TagSelectorDlg;
import common.Tools;
import database.DBHandler;
import video.VideoApp;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;


public class GridMenu extends JMenuBar {
    public GridMenu(TheGrid theGrid) {
        JMenu jm = new JMenu("Menu");

        JMenuItem m1 = new JMenuItem("Add more pictures ...");
        m1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PersistString ps = new PersistString("Images.lastDirectory", System.getProperty("user.home"));
                String lastDirectory = ps.get();
                JFileChooser fc = new JFileChooser();
                File lastPath = new File(lastDirectory);
                if (lastPath.exists() && lastPath.isDirectory()) {
                    fc.setCurrentDirectory(new File(lastDirectory));
                }
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", Tools.getImageExtensions());
                fc.setFileFilter(filter);
                fc.setMultiSelectionEnabled(true);
                if (fc.showOpenDialog(theGrid.rootPane) == JFileChooser.APPROVE_OPTION) {
                    lastDirectory = fc.getCurrentDirectory().getPath();
                    ps.set(lastDirectory);
                    try {
                        theGrid.addImageFilesToDatabase(fc.getSelectedFiles());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        JMenuItem m1_1 = new JMenuItem("Paste more pictures ...");
        m1_1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    java.util.List<File> list  = (java.util.List<File>)clipboard.getData(DataFlavor.javaFileListFlavor);
                    File[] arr = list.toArray(new File[0]);
                    theGrid.addImageFilesToDatabase (arr);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JMenuItem m2 = new JMenuItem("Backup DB ...");
        m2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DBHandler.getInst().backup();
            }
        });

        JMenuItem m3 = searchDupes(0, theGrid, "Search for double Items");
        JMenuItem m31 = searchDupes(1, theGrid, "Delete double Items");

        JMenuItem m4 = new JMenuItem("video App");
        m4.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VideoApp.open(theGrid);
            }
        });

        JMenuItem m5 = new JMenuItem("Tag List");
        m5.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.List<String> list = TagSelectorDlg.xmain(null);
                //System.out.println(list.toString());
                Component[] comps = theGrid.rootPane.getComponents();
                for (Component gi : comps) {
                    GridImage g = (GridImage)gi;
                    g.hide(list);
                }
                theGrid.rootPane.doLayout();
                theGrid.rootPane.repaint();
                //theGrid.refresh();
            }
        });

        JMenuItem m6 = new JMenuItem("view Log");
        m6.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogBox.xmain();
            }
        });

        JMenuItem m7 = new JMenuItem("WebServer");
        m7.addActionListener(new AbstractAction() {
            static WebApp wapp;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (wapp != null) {
                    Sam.speak("Webserver already running.");
                    return;
                }
                wapp = new WebApp();
            }
        });

        jm.add(m1);
        jm.add(m1_1);
        jm.add(m2);
        jm.add(m3);
        jm.add(m31);
        jm.add(m4);
        jm.add(m5);
        jm.add(m6);
        jm.add(m7);
        this.add(jm);
        theGrid.setJMenuBar(this);
    }

    private JMenuItem searchDupes(int mode, TheGrid theGrid, String text) {
        JMenuItem m3 = new JMenuItem(text);
        m3.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder sb = new StringBuilder();
                Component[] components = theGrid.rootPane.getComponents();
                for (int i = 0; i < components.length; i++) {
                    for (int j = i + 1; j < components.length; j++) {
                        GridImage g1 = (GridImage) components[i];
                        GridImage g2 = (GridImage) components[j];
                        if (Arrays.equals(g1.getHash(), g2.getHash())) {
                            sb.append(g1.getRowID()).append(" and ")
                                    .append(g2.getRowID()).append(" are identical\n");
                            if (mode == 1) {
                                if (DBHandler.getInst().deleteImage(g2.getRowID())) {
                                    theGrid.rootPane.remove(g2);
                                    theGrid.rootPane.doLayout();
                                    theGrid.rootPane.repaint();
                                }
                            }

                        }
                    }
                }
                String msg;
                if (sb.isEmpty()) {
                    msg = "No duplicates!";
                } else {
                    msg = sb.toString();
                }
                JOptionPane.showMessageDialog(theGrid, msg, "Search result", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return m3;
    }
}
