package Music;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MyMp3Player {
    public static ArrayList<Thread> al = new ArrayList<>();

    public static void play (File f) throws FileNotFoundException, JavaLayerException {
        try (FileInputStream fis = new FileInputStream(f)) {
            al.add(Thread.ofVirtual().start(new Runnable() {
                @Override
                public void run() {
                    try {
                        new Player(fis).play();
                    } catch (JavaLayerException e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
