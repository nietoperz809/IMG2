package thegrid.gridmenu;

import common.*;
import database.DBHandler;
import dialogs.*;
import httpserv.WebApp;
import org.h2.tools.GUIConsole;
import thegrid.ImageViewController;
import thegrid.TheGrid;
import video.VideoApp;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static common.Tools.restartApplication;

//import static database.DBHandler.RowIDfromImgHash;


public class GridMenuBar extends JMenuBar {

    public GridMenuBar(final TheGrid theGrid) {
        JMenu jm = new JMenu("Menu");
        JMenuItem jmi;

        jm.add (new SubMenuMarked(theGrid));

        jmi = new JMenuItem("Instructions ...");
        jmi.addActionListener(_ -> Manual.start());
        jm.add(jmi);

        jmi = new JMenuItem("Mail ...");
        jmi.addActionListener(_ -> EmailUtil.xmain(null));
        jm.add(jmi);

        jmi = new JMenuItem("Speak Integer");
        jmi.addActionListener(_ -> {
            int num = LineInput.onlyPosNumber("", "Num", Color.MAGENTA);
            String s = NumToText.convert(num);
            Sam.speak(s);
        });
        jm.add(jmi);

        jmi = new JMenuItem("Memory Monitor ...");
        jmi.addActionListener(_ -> new MonitorFrame());
        jm.add(jmi);

        jmi = new ColoredMenuItem("Restart the app ...", Color.RED, Color.WHITE);
        jmi.addActionListener(_ -> {
            DBHandler.log("--- TheGrid ended");
            DBHandler.closeDatabase();
            try {
                Tools.delay(600);
                Tools.restartApplication();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("direct sql command");
        jmi.addActionListener(_ -> {
            String sql = LineInput.xmain("select * from (select name,_ROWID_,tag,accnum from IMAGES) order by _rowid_ desc",
                    "direct SQL", Color.BLUE);
            if (!sql.isEmpty()) {
                boolean b = DBHandler.execSQL(sql);
                System.out.println(b);
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("Open another Grid ...");
        jmi.addActionListener(_ -> (new Thread(() -> {
            String sql = LineInput.xmain("select * from (select name,_ROWID_,tag,accnum from IMAGES) order by _rowid_ desc",
                    "SQL", Color.BLUE);
            if (!sql.isEmpty())
                new TheGrid(sql, "child ");
        })).start());
        jm.add(jmi);

        jmi = new JMenuItem("Dispose all open views ...");
        jmi.addActionListener(_ -> ImageViewController.killAllViews());
        jm.add(jmi);

        jmi = new JMenuItem("Add more pictures ...");
        jmi.addActionListener(_ -> {
            PersistString ps = new PersistString("Images.lastDirectory", System.getProperty("user.home"));
            String lastDirectory = ps.get();
            JFileChooser fc = new JFileChooser();
            File lastPath = new File(lastDirectory);
            if (lastPath.exists() && lastPath.isDirectory()) {
                fc.setCurrentDirectory(new File(lastDirectory));
            }
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", ImageTools.getImageExtensions());
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
        });
        jm.add(jmi);

        jmi = new JMenuItem("Paste more pictures ...");
        jmi.addActionListener(_ -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            try {
                clipboard.getData(DataFlavor.javaFileListFlavor);

                List<File> list = (ArrayList<File>) clipboard.getData(DataFlavor.javaFileListFlavor);
                File[] arr = list.toArray(new File[0]);
                theGrid.addImageFilesToDatabase(arr);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("Backup DB ...");
        jmi.addActionListener(_ -> {
            DBHandler.backupDatabase();
            try {
                restartApplication();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        jm.add(jmi);

        jmi = new ColoredMenuItem("Open video App", Color.BLUE, Color.WHITE);
        jmi.addActionListener(_ -> VideoApp.open(theGrid));
        jm.add(jmi);

        jmi = new JMenuItem("Tag List");
        jmi.addActionListener(_ -> TagSelectorDlg.worker_for_tagList());
        jm.add(jmi);

        jmi = new JMenuItem("view Log");
        jmi.addActionListener(_ -> LogBox.xmain());
        jm.add(jmi);

        jmi = new JMenuItem("SQL Console");
        jmi.addActionListener(_ -> {
            try {
                GUIConsole.main();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        jm.add(jmi);

        jmi = new JMenuItem("x*x Grid preview");
        jmi.addActionListener(_ -> {
            BufferedImage big = ImageTools.createPreviewImage(theGrid.imageL, 5);
            ImageViewer.xmain(big);
        });
        jm.add(jmi);

        jmi = new JMenuItem("Another Grid");
        jmi.addActionListener(_ -> {
            SqlList.xmain();
//            String newSql = LineInput.xmain (theGrid.imageL.getSql(),"newSQL", Color.BLUE);
//            if (!newSql.isEmpty()) {
//                (new Thread(() -> new TheGrid(newSql, "WORKER"))).start();
//            }
        });
        jm.add(jmi);

        final JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("WebServer");
        cbMenuItem.addActionListener(new ActionListener() {
            static WebApp wapp;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (wapp != null) {
                    Sam.speak("Webserver already running.");
                    return;
                }
                wapp = new WebApp();
                cbMenuItem.setState(true);
            }
        });
        jm.add(cbMenuItem);

        jm.add (DirectoryWatcher.createMenuItem(theGrid));

        final JMenuItem jmi2 = new JCheckBoxMenuItem("save image history");
        jmi2.addActionListener(_ -> {
            if (!jmi2.isSelected()) {
                theGrid.setHistoryPath(null);
                return;
            }
            String dir = MsgBox.chooseDir(theGrid);
            theGrid.setHistoryPath(dir);
        });
        jm.add(jmi2);

        this.add(jm);
        theGrid.setJMenuBar(this);
    }
}
