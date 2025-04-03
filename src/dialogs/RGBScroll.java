package dialogs;

import Catalano.Imaging.FastBitmap;
import thegrid.ImgPanel;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class RGBScroll extends JDialog {
    private JPanel contentPane;
    private JSlider sliderRed;
    private JSlider sliderGreen;
    private JSlider sliderBlue;
    private FastBitmap image;
    private ImgPanel panel;

    public void setImage (BufferedImage img, ImgPanel imgPanel) {
        panel = imgPanel;
        image = new FastBitmap(img);
    }

    public RGBScroll() {
        setContentPane(contentPane);
        setModal(true);
        setUndecorated(true);

        sliderRed.setMaximum(255);
        sliderRed.setMinimum(1);
        sliderGreen.setMaximum(255);
        sliderGreen.setMinimum(1);
        sliderBlue.setMaximum(255);
        sliderBlue.setMinimum(1);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        sliderRed.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int v = sliderRed.getValue();
                for (int s=0; s<image.getRGBData().length; s++)
                    image.setRed (s, v);
                panel.setImage(image);
            }
        });

        sliderGreen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int v = sliderGreen.getValue();
                for (int s=0; s<image.getRGBData().length; s++)
                    image.setGreen (s, v);
                panel.setImage(image);
            }
        });

        sliderBlue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int v = sliderBlue.getValue();
                for (int s=0; s<image.getRGBData().length; s++)
                    image.setBlue (s, v);
                panel.setImage(image);
            }
        });
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void xmain(BufferedImage img, ImgPanel imgPanel) {
        RGBScroll dialog = new RGBScroll();
        dialog.setImage(img, imgPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        RGBScroll dialog = new RGBScroll();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        System.exit(0);
    }
}
