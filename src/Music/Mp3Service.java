package Music;

import javazoom.jl.player.Player;

import java.io.File;
import java.io.FileInputStream;

public class Mp3Service {

    private Thread playThread;
    private Player player;

    public void play(File file) {
        playThread = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(file)) {
                player = new Player(fis);
                player.play(); // blockiert
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        playThread.start();
    }

    public void stop() {
        if (player != null) {
            player.close(); // DAS ist der entscheidende Call
        }
    }
}