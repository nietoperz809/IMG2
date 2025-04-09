package dialogs;

import Catalano.Imaging.FastBitmap;
import com.jhlabs.image.HSBAdjustFilter;
import common.Stepper;
import thegrid.ImgPanel;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class RGBScroll extends JDialog {
    private JPanel contentPane;
    private JSlider sliderRed;
    private JSlider sliderGreen;
    private JSlider sliderBlue;
    private BufferedImage image;
    private ImgPanel panel;
    private final Stepper stepper = new Stepper (0.0f, 1.0f, 255);

    public void setImage (BufferedImage img, ImgPanel imgPanel) {
        panel = imgPanel;
        image = img;
    }

    public RGBScroll() {
        setContentPane(contentPane);
        setModal(true);
        setUndecorated(true);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(_-> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        sliderRed.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                doIt();
            }
        });

        sliderGreen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                doIt();
            }
        });

        sliderBlue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                doIt();
            }
        });
    }

    private void doIt() {
        float r = getVal(sliderRed);
        float g = getVal(sliderGreen);
        float b = getVal(sliderBlue);
        System.out.println("r:"+r + " g:"+g + " b"+b);
        HSBAdjustFilter c = new HSBAdjustFilter(r, g, b);
        panel.setImage(c.filter(image, null));
    }

    private float getVal(JSlider sl) {
        int v = sl.getValue();
        return stepper.get(v);
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

//    public static void main(String[] args) {
//        RGBScroll dialog = new RGBScroll();
//        dialog.pack();
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);
//        System.exit(0);
//    }
}
