package thegrid.gridmenu;

import common.*;
import database.DBHandler;
import dialogs.Tagger;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import thegrid.TheGrid;
import thegrid.Thumbnail;

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
        add(jmi);
    }

    private void changeTags(final TheGrid grid, boolean remove) {
        final Thumbnail[] marked = Thumbnail.getMarked(grid);
        String tagsnew = Tagger.tagList("", "Tag:", Color.YELLOW);
        if (tagsnew.isEmpty())
            return;
        TreeSet<String> tnew = Csv.getSetFromCSVString(tagsnew);
        for (Thumbnail gi : marked) {
            int id = gi.getRowID();
            String tags = DBHandler.getTags(id);
            TreeSet<String> tset = Csv.getSetFromCSVString(tags);
            if (remove)
                tset.removeAll(tnew);
            else
                tset.addAll(tnew);
            DBHandler.setTag(id, Csv.CsvStringFromSet(tset));
        }
        Thumbnail.markAll(grid, false);
    }

    public SubMenuMarked(final TheGrid grid) {
        super("Marked ...");

        addItem("Mark all",
                _ -> Thumbnail.markAll(grid, true));

        addItem("Unmark all",
                _ -> Thumbnail.markAll(grid, false));

        addItem("Toggle",
                _ -> Thumbnail.toggleMarks(grid));

        addItem("Delete from DB", _ -> {
            final Thumbnail[] marked = Thumbnail.getMarked(grid);
            final JPanel jp = grid.rootPane;
            for (Thumbnail gi : marked) {
                int id = gi.getRowID();
                DBHandler.deleteSilently("IMAGES",id);
                jp.remove(gi);
            }
            jp.doLayout();
            jp.repaint();
            Sam.speak(marked.length + "files deleted.");
        });

        addItem("Save to Disk",
                _ -> {
                    final Thumbnail[] marked = Thumbnail.getMarked(grid);
                    String outPath = MsgBox.chooseDir(SubMenuMarked.this);
                    for (Thumbnail gi : marked) {
                        int id = gi.getRowID();
                        byte[] b = DBHandler.loadImage(id);
                        String tags = DBHandler.getTagsCommaReplaced(id);
                        BufferedImage b2 = ImageTools.JPGByteArrayToImg(b);
                        ImageTools.saveImg2Disk(b2, id, outPath, tags);
                    }
                    Thumbnail.markAll(grid, false);
                });

        addItem("Make Zip",
                _ -> {
                    ZipParameters zipParameters = Tools.getStandardZipParams();
                    String outPath = MsgBox.chooseDir(SubMenuMarked.this);
                    try {
                        String pwd = RandomWord.generateWord(6);
                        ZipFile zipFile = new ZipFile(outPath + File.separator +
                                System.currentTimeMillis() + "-images.rar",
                                pwd.toCharArray());
                        final Thumbnail[] marked = Thumbnail.getMarked(grid);
                        //DeferredFileDeleter.lock();
                        int repeats = 0;
                        for (Thumbnail gi : marked) {
                            int id = gi.getRowID();
                            BufferedImage b2 = ImageTools.JPGByteArrayToImg(DBHandler.loadImage(id));
                            String tags = DBHandler.getTagsCommaReplaced(id);
                            String imgFile = ImageTools.saveImg2Disk(b2, id, outPath, tags);
                            System.out.println("put on zip: " + imgFile);
                            do {
                                try {
                                    zipFile.addFile(imgFile, zipParameters);
                                    repeats = 0;
                                } catch (ZipException e) {
                                    repeats++;
                                    if (repeats > 1000) {
                                        throw new RuntimeException("Zipper: too many retries!");
                                    }
                                }
                            } while (repeats != 0);
                            DeferredFileDeleter.put(new File(imgFile));
                        }
                        Thumbnail.markAll(grid, false);
                        zipFile.close();
                        //DeferredFileDeleter.unlock();
                        MsgBox.AsyncInfo("Password (posted to clipboard) is: " + pwd);
                        SystemClipboard.setString(pwd);
                    } catch (IOException ex) {
                        //System.out.println("Zipping err: "+ex);
                        throw new RuntimeException(ex);
                    }
                });

        addItem("Add tags",
                _ -> {
                    changeTags(grid, false);
                });

        addItem("Remove tags",
                _ -> {
                    changeTags(grid, true);
                });
    }
}
