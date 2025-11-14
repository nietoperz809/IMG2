package common;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
                            System.out.println("delete of queue entry failed");
                            put (file);
                        }
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

// --Commented out by Inspection START (11/10/2025 3:57 PM):
//    public static void lock() {
//        lock = true;
//    }
// --Commented out by Inspection STOP (11/10/2025 3:57 PM)

// --Commented out by Inspection START (11/10/2025 3:57 PM):
//    public static void unlock() {
//        lock = false;
//    }
// --Commented out by Inspection STOP (11/10/2025 3:57 PM)

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
