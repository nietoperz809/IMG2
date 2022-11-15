package thegrid;

import imageloader.DBHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.prefs.Preferences;

public class GridMenu extends JMenuBar {
    public GridMenu(TheGrid theGrid) {
        JMenu jm = new JMenu("Menu");
        JMenuItem m1 = new JMenuItem("Add more pictures ...");
        JMenuItem m2 = new JMenuItem("Backup DB ...");
        m1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lastDirectory = Preferences.userNodeForPackage(theGrid.rootPane.getClass()).get("Images.lastDirectory", System.getProperty("user.home"));
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
                    Preferences.userNodeForPackage(theGrid.rootPane.getClass()).put("Images.lastDirectory", lastDirectory);
                    try {
                        theGrid.addImageFilesToDatabase(fc.getSelectedFiles());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        m2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DBHandler.getInst().backup();
            }
        });

        jm.add(m1);
        jm.add(m2);
        this.add(jm);
        theGrid.setJMenuBar(this);
    }
}
