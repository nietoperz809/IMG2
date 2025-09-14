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
    private static ImageView imageView;

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
            Scene scene = new Scene(root);

            bridge.setScene(scene);
            bridge.setSize(600,600);

            JFrame frame = new JFrame("JfxImageView");
            frame.setContentPane(panel1);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            frame.setSize(800,800);
        });
    }

    public static void start (BufferedImage img) {
        new JfxImageView(img);
    }

    public static void main(String[] args) {
        new JfxImageView(null);
    }
}

