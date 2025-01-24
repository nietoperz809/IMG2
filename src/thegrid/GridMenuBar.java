package thegrid;

import common.*;
import database.DBHandler;
import dialogs.*;
import httpserv.WebApp;
import video.VideoApp;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;


public class GridMenuBar extends JMenuBar {
    public GridMenuBar(TheGrid theGrid) {
        JMenu jm = new JMenu("Menu");
        JMenuItem jmi;

        JMenu menu2 = new JMenu("Marked ...");
        jmi = new JMenuItem("Save to Disk");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GridImage[] marked = GridImage.getMarked();
                if (marked.length == 0) {
                    Tools.Error("none element marked");
                    return;
                }
                String outPath = Tools.chooseDir(GridMenuBar.this);
                for (GridImage gi : marked) {
                    int id = gi.getRowID();
                    byte[] b = DBHandler.loadImage(id);
                    try {
                        BufferedImage b2 = ImgTools.byteArrayToImg(b);
                        ImgTools.saveImg2Disk(b2, id, outPath);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                GridImage.unmarkAll();
            }
        });
        menu2.add(jmi);
        jm.add(menu2);

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
                DBHandler.backup();
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
                worker_for_tagList();
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
//        m3.addActionListener(new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                StringBuilder sb = new StringBuilder();
//                ArrayList<Integer> rod = DBHandler.getImgRowids();
//                for (int i = 0; i < rod.size(); i++) {
//                    byte[] h1 = DBHandler.loadImgHash(rod.get(i));
//                    if (h1 == null)
//                        continue;
//                    for (int j = i + 1; j < rod.size(); j++) {
//                        byte[] h2 = DBHandler.loadImgHash(rod.get(j));
//                        if (h2 == null)
//                            continue;
//                        if (Arrays.equals(h1,h2)) {
//                            System.out.println("dupe!"+rod.get(i)+"--"+rod.get(j));
//                        }
//                    }
//                }
//            }
//        });

        m3.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    DBHandler.main(null);
                    for (int s = 1; s < 10; s++)
                        DBHandler.loadImgHash(s);
                }
                );
            }
        });

        return m3;
    }

    private void worker_for_tagList() {
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
