package thegrid;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Wincon;
import common.*;
import database.DBHandler;
import dialogs.LineInput;
import dialogs.LogBox;
import dialogs.MonitorFrame;
import dialogs.TagSelectorDlg;
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
        JMenuItem jmi;

        jmi = new JMenuItem("Memory Monitor ...");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MonitorFrame();
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("Restart the app ...");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DBHandler.getInst().log("--- TheGrid ended");
                DBHandler.getInst().close();
                try {
                    Tools.delay(600);
                    TheGrid.restartApplication();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("direct sql command");
        jmi.addActionListener(new AbstractAction() {
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
        jm.add(jmi);

        jmi = new JMenuItem("Open another Grid ...");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                (new Thread(() -> {
                    String sql = LineInput.xmain(TheGrid.mainSQL.get().substring(0, 42),
                            "SQL", Color.BLUE);
                    if (!sql.isEmpty())
                        new TheGrid(sql, "child ");
                })).start();
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("Dispose all open views ...");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                theGrid.controller.killAll();
            }
        });
        jm.add(jmi);


        jmi = new JMenuItem("Add more pictures ...");
        jmi.addActionListener(new AbstractAction() {
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
        jm.add(jmi);

        jmi = new JMenuItem("Paste more pictures ...");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    java.util.List<File> list = (java.util.List<File>) clipboard.getData(DataFlavor.javaFileListFlavor);
                    File[] arr = list.toArray(new File[0]);
                    theGrid.addImageFilesToDatabase(arr);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("Backup DB ...");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DBHandler.getInst().backup();
            }
        });
        jm.add(jmi);

        jmi = searchDupes(false, theGrid, "Search for double Items");
        jm.add(jmi);
        jmi = searchDupes(true, theGrid, "Delete double Items");
        jm.add(jmi);

        jmi = new JMenuItem("video App");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VideoApp.open(theGrid);
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("Tag List");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worker_for_5x();
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("view Log");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogBox.xmain();
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("set Main SQL");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newSql = LineInput.xmain(TheGrid.mainSQL.get(),
                        "newSQL", Color.BLUE);
                if (!newSql.isEmpty())
                    TheGrid.mainSQL.set(newSql);
            }
        });
        jm.add(jmi);

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
        jm.add(m7);

        final JMenuItem jmi2 = new JCheckBoxMenuItem("save image history");
        jmi2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jmi2.isSelected()) {
                    theGrid.setHistoryPath(null);
                    return;
                }
                String dir = Tools.chooseDir(theGrid);
                theGrid.setHistoryPath(dir);
            }
        });
        jm.add(jmi2);

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
                    int res = JOptionPane.showConfirmDialog(theGrid, mess, "Warn!", JOptionPane.YES_NO_OPTION);
                    if (res == 0) /*OK*/ {
                        System.exit(-1);
                    }
                    return;
                }
                GridImage g1, g2;
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

    private void worker_for_5x() {
        JList<String> jlist = TagSelectorDlg.open();
        if (jlist == null) // cancelled
            return;
        var list = jlist.getSelectedValuesList();
        boolean andMode = jlist.isOpaque();
        (new Thread(() -> {
            StringBuilder sql = new StringBuilder("select name,_ROWID_,tag,accnum from IMAGES where");
            for (int s = 0; s < list.size(); s++) {
                if (s > 0)
                    sql.append(andMode ? " and" : " or");
                sql.append(" tag like " + "'%").append(list.get(s)).append("%'");
            }
            System.out.println(sql);
            new TheGrid(sql.toString(), "WORKER");
        })).start();
    }
}
