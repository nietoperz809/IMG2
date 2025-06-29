package common;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class Audio {

    public static void playAsyncWave(String name) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = new BufferedInputStream(Objects.requireNonNull(loader.getResourceAsStream(name)));
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(is));
            clip.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void playWave(byte[] data) throws Exception {
        final Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
        InputStream inp = new BufferedInputStream(new ByteArrayInputStream(data));
        clip.open(AudioSystem.getAudioInputStream(inp));
        clip.start();
    }

    public static void playWave(InputStream is) {
        CountDownLatch syncLatch = new CountDownLatch(1);
        try {
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(e ->
            {
                if (e.getType() == LineEvent.Type.STOP) {
                    syncLatch.countDown();
                }
            });
            clip.open(AudioSystem.getAudioInputStream(is));
            clip.start();
            syncLatch.await();
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    public static InputStream loadWav (String  name) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return new BufferedInputStream(Objects.requireNonNull(loader.getResourceAsStream(name)));
    }

    public static void playWaveFromResource (String name)
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = new BufferedInputStream(Objects.requireNonNull(loader.getResourceAsStream(name)));
        playWave (is);
    }


}
