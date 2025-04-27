package dialogs;

import com.jhlabs.image.HSBAdjustFilter;
import common.Stepper;
import thegrid.ImgPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class RGBScroll extends JDialog {
    private JPanel contentPane;
    private JSlider sliderRed;
    private JSlider sliderGreen;
    private JSlider sliderBlue;
    private JLabel labBlue;
    private JLabel labGreen;
    private JLabel labRed;
    private BufferedImage image;
    private ImgPanel imgPanel;
    private final Stepper stepper = new Stepper (0.0f, 1.0f, 255);

    public void setImage (BufferedImage img, ImgPanel imgPanel) {
        this.imgPanel = imgPanel;
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

        sliderGreen.addChangeListener(_ -> doIt());

        sliderBlue.addChangeListener(_ -> doIt());

        sliderRed.addChangeListener(_ -> doIt());
    }

    private void doIt() {
        float r = getVal(sliderRed);
        float g = getVal(sliderGreen);
        float b = getVal(sliderBlue);
        labRed.setText(Float.toString(r));
        labGreen.setText(Float.toString(g));
        labBlue.setText(Float.toString(b));
        imgPanel.setImage(new HSBAdjustFilter(r, g, b).filter(image, null));
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
