package thegrid;

import common.*;
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

        JMenuItem m8 = new JMenuItem ("Restart the app ...");
        m8.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DBHandler.getInst().log("--- TheGrid ended");
                DBHandler.getInst().close();
                try {
                    Thread.sleep(600);
                    TheGrid.restartApplication();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JMenuItem mDSQL = new JMenuItem ("direct sql command");
        mDSQL.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = LineInput.xmain(TheGrid.mainSQL.get().substring(0, 42),
                        "direct SQL", Color.BLUE);
                if (!sql.isEmpty()) {
                    boolean b = DBHandler.getInst().execSQL(sql);
                    System.out.println(b);
                }
            }
        });

        JMenuItem m00 = new JMenuItem ("Open another Grid ...");
        m00.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                (new Thread(() -> {
                    String sql = LineInput.xmain (TheGrid.mainSQL.get().substring(0,42),
                            "SQL", Color.BLUE);
                    if (!sql.isEmpty())
                        new TheGrid(sql);
                })).start();                                }
        });

        JMenuItem m0 = new JMenuItem ("Dispose all open views ...");
        m0.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                theGrid.controller.killAll();
            }
        });


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
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", ImgTools.getImageExtensions());
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

        JMenuItem m3 = searchDupes(false, theGrid, "Search for double Items");
        JMenuItem m31 = searchDupes(true, theGrid, "Delete double Items");

        JMenuItem m4 = new JMenuItem("video App");
        m4.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VideoApp.open(theGrid);
            }
        });

        class worker_for_5x {
            worker_for_5x(String conn) {
                java.util.List<String> list = TagSelectorDlg.open();
                (new Thread(() -> {
                    String sql = "select name,_ROWID_,tag,accnum from IMAGES where";
                    for (int s=0; s<list.size(); s++) {
                        if (s>0)
                            sql += conn;
                        sql += " tag like "+"'%"+list.get(s)+"%'";
                    }
                    System.out.println(sql);
                    new TheGrid(sql);
                })).start();            }
        }

        JMenuItem m5 = new JMenuItem("Tag List (or)");
        m5.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new worker_for_5x(" or ");
            }
        });

        JMenuItem m51 = new JMenuItem("Tag List (and)");
        m51.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new worker_for_5x(" and ");
            }
        });

        JMenuItem m6 = new JMenuItem("view Log");
        m6.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogBox.xmain();
            }
        });

        JMenuItem msql = new JMenuItem("set Main SQL");
        msql.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newSql = LineInput.xmain(TheGrid.mainSQL.get(),
                        "newSQL", Color.BLUE);
                if (!newSql.isEmpty())
                    TheGrid.mainSQL.set (newSql);
            }
        });


        JCheckBoxMenuItem m7 = new JCheckBoxMenuItem("WebServer");
        m7.addActionListener(new AbstractAction() {
            static WebApp wapp;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (wapp != null) {
                    Sam.speak("Webserver already running.");
                    return;
                }
                wapp = new WebApp();
                m7.setState(true);
            }
        });

        JCheckBoxMenuItem mHist = new JCheckBoxMenuItem("save image history");
        mHist.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!mHist.isSelected()) {
                    theGrid.setHistoryPath(null);
                    return;
                }
                String dir = Tools.chooseDir(theGrid);
                theGrid.setHistoryPath(dir);
            }
        });

        jm.add (mDSQL);
        jm.add(m00);
        jm.add(m0);
        jm.add(m1);
        jm.add(m1_1);
        jm.add(m2);
        jm.add(m3);
        jm.add(m31);
        jm.add(m4);
        jm.add(m5);
        jm.add(m51);
        jm.add(m6);
        jm.add(m7);
        jm.add(m8);
        jm.add (mHist);
        jm.add (msql);
        this.add(jm);
        theGrid.setJMenuBar(this);
    }

    private JMenuItem searchDupes(boolean searchOnly, TheGrid theGrid, String text) {
        JMenuItem m3 = new JMenuItem(text);
        m3.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder sb = new StringBuilder();
                Component[] components = theGrid.rootPane.getComponents();
                if (components.length != theGrid.imageL.allFiles.size()) {
                    String mess = "Please restart and wait until all " + theGrid.imageL.allFiles.size() + " tiles are loaded!";
                    int res = JOptionPane.showConfirmDialog (theGrid, mess, "Warn!", JOptionPane.YES_NO_OPTION);
                    if (res == 0) /*OK*/ {
                        System.exit(-1);
                    }
                    return;
                }
                GridImage g1,g2;
                for (int i = 0; i < components.length; i++) {
                    for (int j = i + 1; j < components.length; j++) {
                        g1 = (GridImage) components[i];
                        g2 = (GridImage) components[j];
                        byte[] h1 = g1.getHash();
                        byte[] h2 = g2.getHash();
                        if (Arrays.equals(h1, h2)) {
                            sb.append(g1.getRowID()).append(":")
                                    .append(g2.getRowID()).append(" * ");
                            if (!searchOnly) {
                                if (DBHandler.getInst().deleteImage(g2.getRowID())) {
                                    theGrid.rootPane.remove(g2);
                                }
                            }
                        }
                    }
                }
                theGrid.rootPane.doLayout();
                theGrid.rootPane.repaint();
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
