package common;

import java.nio.file.*;

public class DirectoryWatcher {

    private WatchKey key;

    public void stop() {
        System.out.println("dwatch stop");
        key = null;
    }

    public void start (String dir) throws Exception {
        System.out.println("dwatch start");
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(dir);
        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        Tools.runTask(() -> {
            while (true) {
                try {
                    if ((key = watchService.take()) == null)
                        break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println(
                            "Event kind:" + event.kind()
                                    + ". File affected: " + event.context() + ".");
                }
                key.reset();
            }
        });

    }
}