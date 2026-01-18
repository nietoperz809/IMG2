package common;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DeferredFileDeleter {
    private static final BlockingQueue<File> __delQue = new ArrayBlockingQueue<>(500);

    public static void shellDelFile (String filePath) throws Exception{
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "del", "/f", "/q", filePath);
        Process process = processBuilder.start();
        if (process.waitFor() != 0)
            throw new Exception("Can't delete");
    }

    static {
        Tools.runTask(() -> {
            while (true) {
                try {
                    File file = __delQue.take();
                    Tools.runTask(() -> {
                        try {
                            Thread.sleep(1000);
                            String fp = file.getPath();
                            System.out.println("del: "+fp);
                            shellDelFile(fp);
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
