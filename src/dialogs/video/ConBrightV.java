package dialogs.video;

import common.Stepper;
import uk.co.caprica.vlcj.player.base.VideoApi;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConBrightV extends JDialog {
    private JPanel contentPane;
    private JSlider conSlider;
    private JSlider briSlider;
    private JLabel conLabel;
    private JLabel briLabel;
    private JSlider satSlider;
    private JLabel satLabel;
    private JLabel hueLabel;
    private JSlider hueSlider;
    private VideoApi vapi;

    public ConBrightV() {
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

        briSlider.addChangeListener(_ -> {
            final Stepper stepper = new Stepper(0.0f, 2.0f, 256);
            float v = stepper.get(briSlider.getValue());
            briLabel.setText(Float.toString(v));
            vapi.setBrightness(v);
        });

        conSlider.addChangeListener(_ -> {
            final Stepper stepper = new Stepper(0.0f, 2.0f, 256);
            float v = stepper.get(conSlider.getValue());
            conLabel.setText(Float.toString(v));
            vapi.setContrast(v);
        });

        satSlider.addChangeListener(_ -> {
            final Stepper stepper = new Stepper(0.01f, 3.0f, 256);
            float v = stepper.get(satSlider.getValue());
            satLabel.setText(Float.toString(v));
            vapi.setSaturation(v);
        });

        hueSlider.addChangeListener(_ -> {
            final Stepper stepper = new Stepper(-180.01f, 180.0f, 256);
            float v = stepper.get(hueSlider.getValue());
            hueLabel.setText(Float.toString(v));
            vapi.setHue(v);
        });

    }

//    private int getVal(JSlider sl) {
//        int v = sl.getValue();
//        return v;
//    }

//    private void doIt() {
//        float c = getVal(conSlider);
//        float b = getVal(briSlider);
//        float g = getVal (gamSlider);
//        conLabel.setText(Float.toString(c));
//        briLabel.setText(Float.toString(b));
//        gamLabel.setText(Float.toString(g));
//    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void xmain (VideoApi v) {
        ConBrightV dialog = new ConBrightV();
        dialog.vapi = v;
        v.setAdjustVideo(true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
