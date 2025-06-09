package common;

import database.DBHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.FutureTask;

public class DirectoryWatcher {

    FutureTask<?> task;

    public static JCheckBoxMenuItem createMenuItem(Component parent) {
        final JCheckBoxMenuItem dwItem = new JCheckBoxMenuItem("Scan dir for new imgs");
        dwItem.addActionListener(new ActionListener() {
            static DirectoryWatcher dwatch;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!dwItem.getState()) {// not checked
                    dwatch.stop();
                    dwatch = null;
                } else { //checked
                    String dir = MsgBox.chooseDir(parent);
                    if (dir == null) {
                        dwItem.setState(false);
                        return;
                    }
                    dwatch = new DirectoryWatcher();
                    try {
                        dwatch.start(dir);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        return dwItem;
    }

    public void stop() {
        System.out.println("dwatch stop");
        task.cancel(true);
    }

    public void start(final String dir) throws Exception {
        System.out.println("dwatch start");
        task = Tools.runTask(() -> {
            Set<String> intersection = new HashSet<>();
            for (; ; ) {
                try {
                    Set<String> set1 = Tools.listFiles(dir);
                    for (String fname : set1) {
                        File f = new File(dir + File.separator + fname);
                        if (!intersection.isEmpty()) {
                            System.out.println("intersection not empty "+
                                    intersection.size());
                            boolean d = f.delete();
                            if (!d)
                                System.out.println("delete fail in DirectoryWatcher");
                        } else try {
                            // move to DB and delete from disk
                            DBHandler.MoveImageFilesToDB(new File[]{f}, (img, name) -> {
                                Sam.speak("file added");
                            });
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Tools.delay(10000); // next round in 10s
                    Set<String> set2 = Tools.listFiles(dir);
                    intersection = new HashSet<>(set1);
                    intersection.retainAll(set2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

//laladumm1234