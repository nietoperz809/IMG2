package common;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DeferredFileDeleter {
    private final BlockingQueue<File> __que = new ArrayBlockingQueue<>(100);
    private static DeferredFileDeleter singleton;

    public static DeferredFileDeleter getInst() {
        if (singleton == null) {
            singleton = new DeferredFileDeleter();
        }
        return singleton;
    }

    private DeferredFileDeleter() {
        Tools.runTask(() -> {
            while (true) {
                try {
                    File f = __que.take();
                    Tools.runTask(() ->
                            System.out.println(f.delete() ? "delete: " : "fail " +f));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void put (File f) {
        try {
            __que.put(f);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
