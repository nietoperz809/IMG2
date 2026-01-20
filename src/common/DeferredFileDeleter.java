package common;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DeferredFileDeleter {
    private static final BlockingQueue<String> __delQue = new ArrayBlockingQueue<>(500);

    public static void shellDelFile (String filePath) throws Exception{
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "del", "/f", "/q", filePath);
        Process process = processBuilder.start();
        if (process.waitFor() != 0)
            throw new Exception("Can't delete");
    }

    static {
        Tools.runTask(() -> {
            for (;;) {
                try {
                    String filePath = __delQue.take();
                    File f = new File (filePath);
                    Tools.runTask(() -> {
                        System.out.println("del: "+filePath);
                        try {
                            Thread.sleep(1000);
                            shellDelFile(filePath);
                            Thread.sleep(1000);
                            if (f.exists())
                                __delQue.put(filePath);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

//    public static void put(String s ) {
//        put(new File(s));
//    }
//
    public static void put(String s) {
        if (__delQue.contains(s)) {
            System.out.println(s +": already queued");
            return;
        }
        try {
            __delQue.put(s);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
