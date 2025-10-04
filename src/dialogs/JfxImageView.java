package dialogs;

import javafx.embed.swing.JFXPanel;

import javax.swing.*;

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
    private JSlider sliderContrast;
    private JSlider sliderBrightness;
    private JSlider sliderHue;
    private JSlider sliderSaturation;
    private ImageView imgV;
    private Scene scene;

    public JfxImageView (final thegrid.ImgPanel out, final BufferedImage inImg) {

        Platform.runLater(() -> {
            Image image = SwingFXUtils.toFXImage(inImg, null);
            imgV = new ImageView (image);
            imgV.setPreserveRatio(true);
            imgV.setSmooth(true);

            StackPane root = new StackPane(imgV);
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

        checkBox_preserve.setSelected(true);

        slider1.addChangeListener(_ -> Platform.runLater(() -> imgV.setRotate(slider1.getValue())));

        slider2.addChangeListener(_ -> Platform.runLater(() -> {
            int n = 10*slider2.getValue();
            if (checkBox.isSelected())
                imgV.setFitHeight(n);
            else
                imgV.setFitWidth(n);
        }));

        checkBox_preserve.addActionListener(e -> imgV.setPreserveRatio(checkBox_preserve.isSelected()));

        sliderContrast.addChangeListener(_ -> {
            final ColorAdjust colorAdjust = new ColorAdjust();
            double v = getAdjustValue(sliderContrast);
            colorAdjust.setContrast (v);
            apply(colorAdjust);
        });

        sliderBrightness.addChangeListener(_ -> {
            final ColorAdjust colorAdjust = new ColorAdjust();
            double v = getAdjustValue(sliderBrightness);
            colorAdjust.setBrightness (v);
            apply(colorAdjust);
        });

        sliderHue.addChangeListener(_ -> {
            final ColorAdjust colorAdjust = new ColorAdjust();
            double v = getAdjustValue(sliderHue);
            colorAdjust.setHue (v);
            apply(colorAdjust);
        });

        sliderSaturation.addChangeListener(_ -> {
            final ColorAdjust colorAdjust = new ColorAdjust();
            double v = getAdjustValue(sliderSaturation);
            colorAdjust.setSaturation (v);
            apply(colorAdjust);
        });
    }

    private void apply (ColorAdjust ca) {
        Platform.runLater(() -> {
            imgV.setEffect(ca);
        });
    }

    private double getAdjustValue(JSlider sl) {
        double v = 1.0-((double)sl.getValue()/50.0);
        System.out.println(v);
        return v;
    }

    public BufferedImage getTransformedImg() {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<BufferedImage> ret = new AtomicReference<>();
        Platform.runLater(() -> {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT); // kein weißer Hintergrund
            WritableImage fxImage = imgV.snapshot(params, null);
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

