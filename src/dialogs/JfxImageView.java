package dialogs;

import javafx.embed.swing.JFXPanel;

import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;


public class JfxImageView {
    private JPanel panel1;
    private JFXPanel bridge;
    private JScrollPane scroller;
    private JPanel bridgepanel;
    private JSlider slider1;
    private JSlider slider2;
    private JCheckBox checkBox;
    private ImageView imageView;
    private Scene scene;

    class ImageViewToBufferedImage {
        public static BufferedImage getTransformedImage(ImageView imageView) {
            SnapshotParameters params = new SnapshotParameters();
            WritableImage fxImage = imageView.snapshot(params, null);
            return SwingFXUtils.fromFXImage(fxImage, null);
        }
    }

    public JfxImageView (final BufferedImage inImg) {

        Platform.runLater(() -> {
            if (inImg == null) { // no image
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                InputStream is1 = classloader.getResourceAsStream("transp.png");
                Image img1 = new Image(is1);
                imageView = new ImageView(img1);
            }
            else {
                Image image = SwingFXUtils.toFXImage(inImg, null);
                imageView = new ImageView (image);
            }
            //imageView.setPreserveRatio(true);

            StackPane root = new StackPane(imageView);
            scene = new Scene(root);

            bridge.setScene(scene);
            bridge.setSize(600,600);

            JFrame frame = new JFrame("JfxImageView");
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    frame.setVisible(false);
                }
            });

            frame.setContentPane(panel1);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setVisible(true);
            frame.setSize(800,800);
        });

        slider1.addChangeListener(_ -> Platform.runLater(() -> imageView.setRotate(slider1.getValue())));

        slider2.addChangeListener(_ -> Platform.runLater(() -> {
            int n = 10*slider2.getValue();
            if (checkBox.isSelected())
                imageView.setFitHeight(n);
            else
                imageView.setFitWidth(n);
        }));
    }

//    public static BufferedImage getTransformedImage(ImageView iv) {
//        SnapshotParameters params = new SnapshotParameters();
//        WritableImage fxImage = iv.snapshot(params, null);
//        return SwingFXUtils.fromFXImage(fxImage, null);
//    }

    public BufferedImage getTransformedImg() {
        final CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<BufferedImage> ret = new AtomicReference<>();
        Platform.runLater(() -> {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT); // kein weißer Hintergrund
            WritableImage fxImage = imageView.snapshot(params, null);
            ret.set(SwingFXUtils.fromFXImage(fxImage, null));
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ret.get();
    }

    public static JfxImageView create(BufferedImage img) {
        return new JfxImageView(img);
    }

    public static void main(String[] args) {
        new JfxImageView(null);
    }
}

