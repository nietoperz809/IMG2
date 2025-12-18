package dialogs;

import com.jhlabs.image.AbstractBufferedImageOp;
import com.jhlabs.image.GainFilter;
import com.jhlabs.image.GammaFilter;
import common.Stepper;
import thegrid.ImgPanel;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class MultiSlider extends JDialog {
    private JPanel contentPane;
    private JScrollBar sc1;
    private JScrollBar sc2;
    private JScrollBar sc3;
    private JScrollBar sc4;
    private JLabel la1;
    private JLabel la2;
    private JLabel la3;
    private JLabel la4;
    private BufferedImage img;
    private ImgPanel imgPanel;
    private final GainFilter gf = new GainFilter();
    private final GammaFilter flt = new GammaFilter();

    private void doFilt (AbstractBufferedImageOp op) {
        BufferedImage out = op.filter(img, null);
        imgPanel.setImage(out);
    }

    public MultiSlider() {
        setContentPane(contentPane);
        setModal(true);

        sc1.addAdjustmentListener(_ -> {
            final Stepper stepper = new Stepper(0.0f, 3.0f, 256);
            float val = stepper.get(sc1.getValue());
            la1.setText(""+val);
            flt.setGamma(val);
            doFilt(flt);
        });

        sc2.addAdjustmentListener(_ -> {
            final Stepper stepper = new Stepper(0.0f, 3.0f, 256);
            float val = stepper.get(sc2.getValue());
            la2.setText(""+val);
            gf.setGain(val);
            doFilt(gf);
        });

        sc3.addAdjustmentListener(_ -> {
            final Stepper stepper = new Stepper(0.0f, 3.0f, 256);
            float val = stepper.get(sc3.getValue());
            la3.setText(""+val);
            gf.setBias(val);
            doFilt(gf);
        });

//        sc4.addAdjustmentListener(_ -> {
//            final Stepper stepper = new Stepper(-3.0f, 3.0f, 256);
//            float val = stepper.get(sc4.getValue());
//            la4.setText(""+val);
//            BufferedImage out = ImageTools.contrast(image, val);
//            imgPanel.setImage(out);
//        });
    }

    public static void main(String[] args) {
        MultiSlider dialog = new MultiSlider();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public static void xmain(BufferedImage img, ImgPanel imgPanel) {
        MultiSlider dialog = new MultiSlider();
        dialog.img = img;
        dialog.imgPanel = imgPanel;
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

}


