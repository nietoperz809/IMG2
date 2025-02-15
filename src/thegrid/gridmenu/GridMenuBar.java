package thegrid.gridmenu;

import common.*;
import database.DBHandler;
import dialogs.*;
import httpserv.WebApp;
import org.jetbrains.annotations.NotNull;
import thegrid.ImageList;
import thegrid.TheGrid;
import video.VideoApp;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;

import static database.DBHandler.RowIDfromImgHash;
import static java.util.Objects.requireNonNull;


public class GridMenuBar extends JMenuBar {

    public GridMenuBar(final TheGrid theGrid) {
        JMenu jm = new JMenu("Menu");
        JMenuItem jmi;

        jm.add (new SubMenuMarked(theGrid));

        jmi = new JMenuItem("Instructions ...");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Manual.start();
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("Mail ...");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmailUtil.xmain(null);
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("Speak Integer");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int num = LineInput.onlyPosNumber("", "Num", Color.MAGENTA);
                String s = NumToText.convert(num);
                Sam.speak(s);
            }
        });
        jm.add(jmi);

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
                DBHandler.log("--- TheGrid ended");
                DBHandler.close();
                try {
                    Tools.delay(600);
                    Tools.restartApplication();
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
                String sql = LineInput.xmain("select * from (select name,_ROWID_,tag,accnum from IMAGES) order by _rowid_ dec",
                        "direct SQL", Color.BLUE);
                if (!sql.isEmpty()) {
                    boolean b = DBHandler.execSQL(sql);
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
                    String sql = LineInput.xmain("select * from (select name,_ROWID_,tag,accnum from IMAGES) order by _rowid_ dec",
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
                DBHandler.backup();
            }
        });
        jm.add(jmi);

        jm.add(searchDupes());

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
                TagSelectorDlg.worker_for_tagList();
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

        jmi = new JMenuItem("Another Grid");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newSql = LineInput.xmain(ImageList.mainSQL,
                        "newSQL", Color.BLUE);
                if (!newSql.isEmpty()) {
                    (new Thread(() -> {
                        new TheGrid(newSql, "WORKER");
                    })).start();
                }
                //TheGrid.mainSQL.set(newSql);
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

    private JMenuItem searchDupes() {
        JMenuItem m3 = new JMenuItem("Search for duplicates");
        m3.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<byte[]> allHashes = DBHandler.loadImgHashes();
                HashSet<Integer> foundSet = new HashSet<>();
                for (int s = 0; s < allHashes.size(); s++) {
                    byte[] bs = allHashes.get(s);
                    for (int n = s + 1; n < allHashes.size(); n++) {
                        byte[] bn = allHashes.get(n);
                        if (Arrays.equals(bs, bn)) {
                            List<Integer> li = RowIDfromImgHash(allHashes.get(s));
                            foundSet.addAll(li);
                        }
                    }
                }
                // any found?
                if (foundSet.isEmpty()) {
                    Tools.Info("No Dupes found in "+allHashes.size()+" files!");
                }
                else {
                    StringBuilder sqlFound = new StringBuilder();
                    sqlFound.append("select name,_ROWID_,tag,accnum from IMAGES where ");
                    for (int i : foundSet) {
                        sqlFound.append("_rowid_=").append(i).append(" or ");
                    }
                    sqlFound.setLength(sqlFound.length() - 4);
                    (new Thread(() -> {
                        new TheGrid(sqlFound.toString(), "WORKER");
                    })).start();
                }
                //System.out.println(sqlFound.toString());
            }
        });

        return m3;
    }

//    private void worker_for_tagList() {
//        JList<String> jlist = TagSelectorDlg.open();
//        if (jlist == null) // cancelled
//            return;
//        var list = jlist.getSelectedValuesList();
//        boolean andMode = jlist.isOpaque();
//        (new Thread(() -> {
//            StringBuilder sql = new StringBuilder("select name,_ROWID_,tag,accnum from IMAGES where");
//            for (int s = 0; s < list.size(); s++) {
//                if (s > 0)
//                    sql.append(andMode ? " and" : " or");
//                sql.append(" tag like " + "'%").append(list.get(s)).append("%'");
//            }
//            System.out.println(sql);
//            new TheGrid(sql.toString(), "WORKER");
//        })).start();
//    }
}
