package thegrid;

import common.*;
import database.DBHandler;
import dialogs.*;
import httpserv.WebApp;
import org.jetbrains.annotations.NotNull;
import video.VideoApp;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

import static database.DBHandler.RowIDfromImgHash;
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
                    BufferedImage b2 = ImgTools.byteArrayToImg(b);
                    ImgTools.saveImg2Disk(b2, id, outPath);
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

        jmi = searchDupes("Search for duplicates");
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

    /*
[705, 10817]
[1267, 9965]
[1392, 11102]
[1550, 10280]
[1943, 10956]
[2322, 11328]
[2324, 11326]
[2325, 11336]
[2327, 11329]
[2328, 11333]
[2551, 10330]
[2776, 10309]
[2948, 11217]
[2951, 11219]
[2952, 11214]
[3001, 11286]
[3002, 11293]
[3034, 10691]
[3198, 9212]
[3437, 10800]
[3439, 9690]
[3551, 10896]
[3819, 9733]
[3975, 9970]
[3976, 9969]
[4146, 9999]
[4746, 9864]
[5042, 10286]
[5126, 11116]
[5129, 11121]
[5301, 11112]
[5304, 11113, 11332]
[5304, 11113, 11332]
[5305, 11114, 11324]
[5305, 11114, 11324]
[5306, 11120]
[5400, 10121]
[5515, 10316]
[5516, 10314]
[5642, 11206]
[5666, 10133]
[5719, 10674, 11426]
[5719, 10674, 11426]
[5732, 10946]
[5865, 10662]
[5870, 9906]
[5922, 9479]
[5955, 9754]
[5972, 10689]
[6123, 11302]
[6285, 10776]
[6291, 10811]
[6357, 9356]
[6364, 9211]
[6392, 10664]
[6393, 10666]
[6409, 10216]
[6448, 10763]
[6449, 10764]
[6451, 10875]
[6453, 10876]
[6470, 10803]
[6471, 10336]
[6472, 10112]
[6473, 10338]
[6481, 10845]
[6494, 9924]
[6663, 10726]
[6806, 10144]
[6825, 9547]
[6826, 11109]
[6827, 9568]
[6875, 9261]
[6943, 10705]
[6977, 10297]
[7000, 11044]
[7069, 10352]
[7204, 10819]
[7419, 10759]
[7531, 11314]
[7533, 9968]
[7594, 9377]
[7777, 10822]
[7811, 10340]
[7862, 10118]
[7882, 9827]
[7912, 9931]
[7923, 10242]
[7928, 10243]
[8049, 11192]
[8052, 11191]
[8275, 9209]
[8380, 9907]
[8405, 9421]
[8437, 11058]
[8438, 9442]
[8480, 10068]
[8486, 10782]
[8491, 10113]
[8521, 11287]
[8553, 11413]
[8589, 10865]
[8591, 10867]
[8609, 9684]
[8610, 11240]
[8612, 9780]
[8871, 10200]
[8887, 9689]
[8888, 9688]
[8936, 9255]
[8999, 10091]
[9152, 11415]
[9166, 9887]
[9559, 11315]
[10234, 11445]
[5719, 10674, 11426]
[10768, 11449]
[11001, 11418]
[5304, 11113, 11332]
[5305, 11114, 11324]
[11126, 11313]
[11128, 11343]
[11132, 11319]
[11133, 11316]
[11451, 11453]
     */

    private @NotNull JMenuItem searchDupes(String text) {
        JMenuItem m3 = new JMenuItem(text);
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
