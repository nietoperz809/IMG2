package thegrid.gridmenu;

import common.DeferredFileDeleter;
import common.ImgTools;
import common.Tools;
import database.DBHandler;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import thegrid.GridImage;
import thegrid.TheGrid;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SubMenuMarked extends JMenu {

    public SubMenuMarked (final TheGrid grid) {
        super ("Marked ...");

        JMenuItem jmi;

        jmi = new JMenuItem("Mark all");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GridImage.markAll(grid, true);
            }
        });
        add(jmi);

        jmi = new JMenuItem("Unmark all");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GridImage.markAll(grid, false);
            }
        });
        add(jmi);

        jmi = new JMenuItem("Toggle");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GridImage.toggleMarks(grid);
            }
        });
        add(jmi);

        jmi = new JMenuItem("Save to Disk");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GridImage[] marked = GridImage.getMarked(grid);
                String outPath = Tools.chooseDir(SubMenuMarked.this);
                for (GridImage gi : marked) {
                    int id = gi.getRowID();
                    byte[] b = DBHandler.loadImage(id);
                    BufferedImage b2 = ImgTools.byteArrayToImg(b);
                    ImgTools.saveImg2Disk(b2, id, outPath);
                }
                GridImage.markAll(grid, false);
            }
        });
        add(jmi);

        jmi = new JMenuItem("Make Zip");
        jmi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setEncryptFiles(true);
                zipParameters.setCompressionLevel(CompressionLevel.HIGHER);
                zipParameters.setEncryptionMethod(EncryptionMethod.AES);
                String outPath = Tools.chooseDir(SubMenuMarked.this);
                try {
                    ZipFile zipFile = new ZipFile (outPath+ File.separator +
                            System.currentTimeMillis()+"-images.rar",
                            "imagebase".toCharArray());
                    GridImage[] marked = GridImage.getMarked(grid);
                    for (GridImage gi : marked) {
                        int id = gi.getRowID();
                        BufferedImage b2 = ImgTools.byteArrayToImg(DBHandler.loadImage(id));
                        String imgFile = ImgTools.saveImg2Disk(b2, id, outPath);
                        zipFile.addFile(imgFile,zipParameters);
                        DeferredFileDeleter.put (new File(imgFile));
                    }
                    GridImage.markAll(grid, false);
                    zipFile.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        add(jmi);
    }
}
