package dialogs;

import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;


public class JfxImageView {
    private JPanel panel1;
    private JFXPanel bridge;
    private JScrollPane scroller;
    private JSlider slider1;
    private JSlider slider2;
    private JCheckBox checkBox;
    private JCheckBox checkBox_preserve;
    private JSlider slider3;
    private ImageView imageView;
    private Scene scene;

    public JfxImageView (final thegrid.ImgPanel out, final BufferedImage inImg) {

        Platform.runLater(() -> {
            Image image = SwingFXUtils.toFXImage(inImg, null);
            imageView = new ImageView (image);
            imageView.setSmooth(true);

            StackPane root = new StackPane(imageView);
            scene = new Scene(root);

            bridge.setScene(scene);
            bridge.setSize(600,600);

            JFrame frame = new JFrame("JfxImageView");
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    out.setImageCentered(getTransformedImg());
                    frame.setVisible(false);
                }
            });

            frame.setContentPane(panel1);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setVisible(true);
            frame.setSize(out.getWidth(),out.getHeight());
        });

        slider1.addChangeListener(_ -> Platform.runLater(() -> imageView.setRotate(slider1.getValue())));

        slider2.addChangeListener(_ -> Platform.runLater(() -> {
            int n = 10*slider2.getValue();
            if (checkBox.isSelected())
                imageView.setFitHeight(n);
            else
                imageView.setFitWidth(n);
        }));

        checkBox_preserve.addActionListener(e -> imageView.setPreserveRatio(checkBox_preserve.isSelected()));

        slider3.addChangeListener(_ -> {
            final ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setContrast (((double)slider3.getValue())/100.0);
            imageView.setEffect (colorAdjust);
        });
    }

    public BufferedImage getTransformedImg() {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<BufferedImage> ret = new AtomicReference<>();
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

    public static JfxImageView create (thegrid.ImgPanel out) {
        return new JfxImageView (out, out.getImage());
    }

//    public static void main(String[] args) {
//        new JfxImageView(null);
//    }
}

