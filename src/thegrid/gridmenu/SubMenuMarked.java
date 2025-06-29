package thegrid.gridmenu;

import common.*;
import database.DBHandler;
import dialogs.LineInput;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import thegrid.Thumbnail;
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
                DBHandler.deleteImageSilently(id);
                jp.remove(gi);
            }
            jp.doLayout();
            jp.repaint();
            Sam.speak(marked.length+"files deleted.");
        });

        addItem("Save to Disk",
                _ -> {
                    final Thumbnail[] marked = Thumbnail.getMarked(grid);
                    String outPath = MsgBox.chooseDir(SubMenuMarked.this);
                    for (Thumbnail gi : marked) {
                        int id = gi.getRowID();
                        byte[] b = DBHandler.loadImage(id);
                        String tags = DBHandler.getTagsCommaReplaced(id);
                        BufferedImage b2 = ImageTools.byteArrayToImg(b);
                        ImageTools.saveImg2Disk(b2, id, outPath, tags);
                    }
                    Thumbnail.markAll(grid, false);
                });

        addItem("Add tags",
                _ -> {
                    final Thumbnail[] marked = Thumbnail.getMarked(grid);
                    String tagsnew = LineInput.tagList("", "Tag:", Color.YELLOW);
                    if (tagsnew.isEmpty())
                        return;
                    TreeSet<String> tnew = Csv.SetFromCSVString(tagsnew);
                    for (Thumbnail gi : marked) {
                        int id = gi.getRowID();
                        String tags = DBHandler.getTags(id);
                        TreeSet<String> tset = Csv.SetFromCSVString(tags);
                        tset.addAll(tnew);
                        DBHandler.setTag(id, Csv.CsvStringFromSet(tset));
                    }
                    Thumbnail.markAll(grid, false);
                });

        addItem("Make Zip",
                _ -> {
                    synchronized (this) { // Must be sync'd because async behaviour
                        // of zip library results in exception
                        ZipParameters zipParameters = Tools.getStandardZipParams();
                        String outPath = MsgBox.chooseDir(SubMenuMarked.this);
                        try {
                            String pwd = RandomWord.generateWord(6);
                            ZipFile zipFile = new ZipFile (outPath+ File.separator +
                                    System.currentTimeMillis()+"-images.rar",
                                    pwd.toCharArray());
                            final Thumbnail[] marked = Thumbnail.getMarked(grid);
                            for (Thumbnail gi : marked) {
                                int id = gi.getRowID();
                                BufferedImage b2 = ImageTools.byteArrayToImg(DBHandler.loadImage(id));
                                String tags = DBHandler.getTagsCommaReplaced(id);
                                String imgFile = ImageTools.saveImg2Disk(b2, id, outPath, tags);
                                System.out.println("put on zip: "+imgFile);
                                zipFile.addFile(imgFile,zipParameters);
                                DeferredFileDeleter.put (new File(imgFile));
                            }
                            Thumbnail.markAll(grid, false);
                            zipFile.close();
                            MsgBox.Info("Password (posted to clipboard) is: "+pwd);
                            SystemClipboard.setString(pwd);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
    }
}
