package dialogs;

import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.HSBAdjustFilter;
import common.Stepper;
import thegrid.ImgPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ConBright extends JDialog {
    private JPanel contentPane;
    private JSlider conSlider;
    private JSlider briSlider;
    private JLabel conLabel;
    private JLabel briLabel;
    private final Stepper stepper = new Stepper(0.0f, 3.0f, 256);
    private ImgPanel imgPanel;
    private BufferedImage image;

    public ConBright() {
        setContentPane(contentPane);
        setModal(true);
        //setUndecorated(true);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        briSlider.addChangeListener(_ -> doIt());
        conSlider.addChangeListener(_ -> doIt());
    }

    private float getVal(JSlider sl) {
        int v = sl.getValue();
        return stepper.get(v);
    }

    private void doIt() {
        float c = getVal(conSlider);
        float b = getVal(briSlider);
        conLabel.setText(Float.toString(c));
        briLabel.setText(Float.toString(b));
        ContrastFilter cfilt = new ContrastFilter();
        cfilt.setContrast(c);
        cfilt.setBrightness(b);
        imgPanel.setImage(cfilt.filter(image, null));
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void xmain(BufferedImage img, ImgPanel imgPanel) {
        ConBright dialog = new ConBright();
        dialog.setImage(img, imgPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void setImage(BufferedImage img, ImgPanel imgPanel) {
        this.imgPanel = imgPanel;
        this.image = img;

    }
}
