package Music;

import javazoom.jl.player.Player;

import java.io.File;
import java.io.FileInputStream;

public class Mp3Service {
    private Player player;

    /**
     * Play MP3
     * @param file the file
     */
    public void play(File file) {
        Thread playThread = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(file)) {
                player = new Player(fis);
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        playThread.start();
    }

    /**
     * Stop playing
     */
    public void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
    }
}