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
import java.util.TreeSet;

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

        addItem("Add tags",
                _ -> {
                    GridImage[] marked = GridImage.getMarked(grid);
                    String tagsnew = LineInput.tagList("", "Tag:", Color.YELLOW);
                    if (tagsnew.isEmpty())
                        return;
                    TreeSet<String> tnew = Tools.SetFromCSVString(tagsnew);
                    for (GridImage gi : marked) {
                        int id = gi.getRowID();
                        String tags = DBHandler.getTags(id);
                        TreeSet<String> tset = Tools.SetFromCSVString(tags);
                        tset.addAll(tnew);
                        DBHandler.setTag(id, Tools.CsvStringFromSet(tset));
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
