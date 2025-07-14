package common;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;

public class DeferredFileDeleter {
    private static final BlockingQueue<File> __delQue = new ArrayBlockingQueue<>(500);
    private static volatile boolean lock;

    static {
        Tools.runTask(() -> {
            while (true) {
                try {
                    if (lock) {
                        Thread.sleep(1000);
                        continue;
                    }
                    File file = __delQue.take();
                    Tools.runTask(() -> {
                        boolean del = file.delete();
                        if (!del) {
                            System.out.println("delete of queue etry failed");
                            put (file);
                        }
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void lock() {
        lock = true;
    }

    public static void unlock() {
        lock = false;
    }

    public static void put(String s ) {
        put(new File(s));
    }

    public static void put(File f) {
        if (__delQue.contains(f)) {
            System.out.println(f+" already queued");
            return;
        }
        try {
            __delQue.put(f);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
