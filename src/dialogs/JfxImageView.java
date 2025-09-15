package dialogs;

import javafx.embed.swing.JFXPanel;

import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.awt.image.BufferedImage;
import java.io.InputStream;


public class JfxImageView {
    private JPanel panel1;
    private JFXPanel bridge;
    private JScrollPane scroller;
    private JPanel bridgepanel;
    private JSlider slider1;
    private JSlider slider2;
    private ImageView imageView;
    private Scene scene;

    public JfxImageView (final BufferedImage inImg) {

        Platform.runLater(() -> {
            if (inImg == null) {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                InputStream is1 = classloader.getResourceAsStream("transp.png");
                Image img1 = new Image(is1);
                imageView = new ImageView(img1);
            }
            else {
                Image image = SwingFXUtils.toFXImage(inImg, null);
                imageView = new ImageView (image);
            }
            imageView.setPreserveRatio(true);

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

        slider1.addChangeListener(e -> {
            Platform.runLater(() -> imageView.setRotate(slider1.getValue()));
        });
    }

    public static void start (BufferedImage img) {
        new JfxImageView(img);
    }

    public static void main(String[] args) {
        new JfxImageView(null);
    }
}

