package thegrid;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PersistString {
    private final String fileName;

    public PersistString (String  name) {
        fileName = name;
    }

    public String get() {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(fileName));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    public String getAndInit(String init) {
        String ret = get();
        if (ret == null) {
            set (init);
            return init;
        }
        return ret;
    }

    public void set(String x) {
        try {
            Files.write( Paths.get(fileName), x.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("write fail");
        }
    }

//    public static void main(String[] args) {
//        thegrid.PersistString p = new thegrid.PersistString("testfile");
//        System.out.println(p.get());
//        p.set("hello world");
//        System.out.println(p.get());
//    }

}
