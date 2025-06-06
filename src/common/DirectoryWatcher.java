package common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.*;

public class DirectoryWatcher {

    private WatchKey watchKey;
    private boolean stopflag = false;

    public static JCheckBoxMenuItem createMenuItem(Component parent) {
        final JCheckBoxMenuItem dwItem = new JCheckBoxMenuItem("DirectoryWatch");
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
        stopflag = true;
    }

    public void start (String dir) throws Exception {
        System.out.println("dwatch start");
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(dir);
        path.register (watchService, StandardWatchEventKinds.ENTRY_CREATE);
//                StandardWatchEventKinds.ENTRY_DELETE,
//                StandardWatchEventKinds.ENTRY_MODIFY);

        Tools.runTask(() -> {
            do {
                watchKey = watchService.poll();
                if (watchKey == null) {
                    Tools.delay(500);
                    continue;
                }
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    System.out.println(
                            "Event kind:" + event.kind()
                                    + ". File affected: " + event.context() + ".");
                }
                watchKey.reset();
            } while (!stopflag);
            System.out.println("leave watcher task");
        });

    }
}