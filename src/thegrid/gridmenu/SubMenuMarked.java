package thegrid.gridmenu;

import common.DeferredFileDeleter;
import common.ImgTools;
import common.Tools;
import database.DBHandler;
import dialogs.LineInput;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import thegrid.GridImage;
import thegrid.TheGrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SubMenuMarked extends JMenu {

    private void addItem(String mtext, ActionListener aa) {
        JMenuItem jmi = new JMenuItem(mtext);
        jmi.addActionListener(aa);
        add (jmi);
    }

    public SubMenuMarked (final TheGrid grid) {
        super ("Marked ...");

        addItem("Mark all",
                _ -> GridImage.markAll(grid, true));

        addItem("Unmark all",
                _ -> GridImage.markAll(grid, false));

        addItem("Toggle",
                _ -> GridImage.toggleMarks(grid));

        addItem("Toggle",
                _ -> GridImage.toggleMarks(grid));

        addItem("Delete from DB", _ -> {
            GridImage[] marked = GridImage.getMarked(grid);
            for (GridImage gi : marked) {
                int id = gi.getRowID();
                DBHandler.deleteImageSilently(id);
            }
        });

        addItem("Save to Disk",
                _ -> {
                    GridImage[] marked = GridImage.getMarked(grid);
                    String outPath = Tools.chooseDir(SubMenuMarked.this);
                    for (GridImage gi : marked) {
                        int id = gi.getRowID();
                        byte[] b = DBHandler.loadImage(id);
                        BufferedImage b2 = ImgTools.byteArrayToImg(b);
                        ImgTools.saveImg2Disk(b2, id, outPath);
                    }
                    GridImage.markAll(grid, false);
                });

        addItem("Set same tags",
                _ -> {
                    GridImage[] marked = GridImage.getMarked(grid);
                    String tag = LineInput.tagList("", "Tag:", Color.YELLOW)
                            .trim().toLowerCase();
                    if (tag.isEmpty())
                        return;
                    for (GridImage gi : marked) {
                        int id = gi.getRowID();
                        DBHandler.setTag(id, tag);
                    }
                    GridImage.markAll(grid, false);
                });

        addItem("Make Zip",
                _ -> {
                    ZipParameters zipParameters = Tools.getStandardZipParams();
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
                });
    }
}
