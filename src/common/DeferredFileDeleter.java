package common;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DeferredFileDeleter {
    private static final BlockingQueue<File> __delQue = new ArrayBlockingQueue<>(100);

    static {
        Tools.runTask(() -> {
            while (true) {
                try {
                    File file = __delQue.take();
                    Tools.runTask(() -> {
                        boolean del = file.delete();
                        if (!del) { // del failed
                            put (file);
                        }
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void put(File f) {
        try {
            __delQue.put(f);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
