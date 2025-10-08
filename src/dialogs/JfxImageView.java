package dialogs;

import common.MsgBox;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.*;
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
    private JSlider sliderRotate;
    private JSlider sliderSize;
    private JCheckBox checkBox;
    private JCheckBox checkBox_preserve;
    private JSlider sliderContrast;
    private JSlider sliderBrightness;
    private JSlider sliderHue;
    private JSlider sliderSaturation;
    private JButton resButton;
    private JButton testButton1;
    private JFXPanel jfx;
    private JButton bpPerspective;
    private ImageView imgV;
    private Scene scene;
    private ColorAdjust colorAdjust = new ColorAdjust();
    private final int HALF = 50;
    // --Commented out by Inspection (10/6/2025 10:36 AM):private final int FULL = 100;

    public JfxImageView (final thegrid.ImgPanel out, final BufferedImage inImg) {

        Platform.runLater(() -> {
            Image image = SwingFXUtils.toFXImage(inImg, null);
            imgV = new ImageView (image);
            imgV.setPreserveRatio(true);
            imgV.setSmooth(true);

            StackPane root = new StackPane(imgV);
            scene = new Scene(root);

            jfx.setScene(scene);
            jfx.setSize(600,600);

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

        sliderRotate.addChangeListener(_ -> Platform.runLater(() -> imgV.setRotate(sliderRotate.getValue())));

        sliderSize.addChangeListener(_ -> Platform.runLater(() -> {
            int n = 10* sliderSize.getValue();
            if (checkBox.isSelected())
                imgV.setFitHeight(n);
            else
                imgV.setFitWidth(n);
        }));

        checkBox_preserve.addActionListener(e -> imgV.setPreserveRatio(checkBox_preserve.isSelected()));

        sliderContrast.addChangeListener(_ -> {
            double v = getAdjustValue(sliderContrast);
            colorAdjust.setContrast (v);
            applyCA();
        });

        sliderBrightness.addChangeListener(_ -> {
            double v = getAdjustValue(sliderBrightness);
            colorAdjust.setBrightness (v);
            applyCA();
        });

        sliderHue.addChangeListener(_ -> {
            double v = getAdjustValue(sliderHue);
            colorAdjust.setHue (v);
            applyCA();
        });

        sliderSaturation.addChangeListener(_ -> {
            double v = getAdjustValue(sliderSaturation);
            colorAdjust.setSaturation (v);
            applyCA();
        });

        resButton.addActionListener(_ -> {
            colorAdjust = new ColorAdjust();
            sliderBrightness.setValue(HALF);
            sliderContrast.setValue(HALF);
            sliderHue.setValue(HALF);
            sliderSaturation.setValue(HALF);
            applyCA();
        });

        testButton1.addActionListener(_ -> {
            PerspectiveTransform ppT = PersValueBox.get();//new PerspectiveTransform();
//            ppT.setUlx(10.0);
//            ppT.setUly(10.0);
//            ppT.setUrx(310.0);
//            ppT.setUry(40.0);
//            ppT.setLrx(310.0);
//            ppT.setLry(60.0);
//            ppT.setLlx(10.0);
//            ppT.setLly(90.0);
            Platform.runLater(() -> setEffect(ppT)); //imgV.setEffect(colorAdjust));
        });

        bpPerspective.addActionListener(e -> {
            MsgBox.Info ("jallo");
        });
    }

    private void setEffect (Effect ef0) {
        Effect ef1 = imgV.getEffect();
        if (ef1 == null || ef1 == ef0)
            imgV.setEffect(ef0);
        else {
            if (ef1 instanceof ColorAdjust)
                ((ColorAdjust)ef1).setInput(ef0);
            imgV.setEffect(ef1);
        }
    }


    private void applyCA() {
        Platform.runLater(() -> setEffect(colorAdjust)); //imgV.setEffect(colorAdjust));
    }

    /**
     * Convert 0...100 to -1.0...+1.0
     * @param sl Slider to use
     * @return double value -1 to 1
     */
    private double getAdjustValue(JSlider sl) {
        double v = (double)(sl.getValue()-HALF)/HALF;
        System.out.println(v);
        return v;
    }

    public BufferedImage getTransformedImg() {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<BufferedImage> ret = new AtomicReference<>();
        Platform.runLater(() -> {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT); // no white background
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
}

