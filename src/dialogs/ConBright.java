package dialogs;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.GammaCorrection;
import com.jhlabs.image.ContrastFilter;
import common.Stepper;
import thegrid.ImgPanel;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import static common.Tools.loomThread;

public class ConBright extends JDialog {
    private JPanel contentPane;
    private JSlider conSlider;
    private JSlider briSlider;
    private JLabel conLabel;
    private JLabel briLabel;
    private JButton resetButton;
    private JSlider gammaSlider;
    private JLabel gammaLabel;
    private JButton takeButton;
    private final Stepper stepper = new Stepper(0.0f, 3.0f, 256);
    private ImgPanel imgPanel;
    private BufferedImage origImg;
    private BufferedImage outImg;

    public ConBright() {
        setContentPane(contentPane);
        setModal(true);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(_ -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        briSlider.addChangeListener(_ -> doIt());
        conSlider.addChangeListener(_ -> doIt());

        resetButton.addActionListener(_ -> {
            conSlider.setValue(100);
            briSlider.setValue(100);
            gammaSlider.setValue(100);
            imgPanel.setImage(origImg);
        });

        gammaSlider.addChangeListener(_ -> {
            float f = getVal(gammaSlider);
            gammaLabel.setText(Float.toString(f));
            GammaCorrection cor = new GammaCorrection(f);
            FastBitmap fb = new FastBitmap(origImg);
            cor.applyInPlace(fb);
            imgPanel.setImage(fb);
            outImg = fb.toBufferedImage();
        });

        takeButton.addActionListener(_ -> origImg = outImg);
    }

    private float getVal(JSlider sl) {
        int v = sl.getValue();
        return stepper.get(v);
    }

    private void doIt() {
        loomThread(() -> {
            float c = getVal(conSlider);
            float b = getVal(briSlider);
            conLabel.setText(Float.toString(c));
            briLabel.setText(Float.toString(b));
            ContrastFilter cfilt = new ContrastFilter();
            cfilt.setContrast(c);
            cfilt.setBrightness(b);
            outImg = cfilt.filter(origImg, null);
            imgPanel.setImage(outImg);
        });
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void xmain(BufferedImage img, ImgPanel imgPanel) {
        ConBright dialog = new ConBright();
        dialog.imgPanel = imgPanel;
        dialog.origImg = img;
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
