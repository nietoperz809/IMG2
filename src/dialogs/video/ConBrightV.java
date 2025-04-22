package dialogs.video;

import common.Stepper;
import uk.co.caprica.vlcj.player.base.VideoApi;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;
import java.util.function.Function;

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
    private JSlider scaleSlider;
    private JLabel scaleLabel;
    private VideoApi vapi;

    public ConBrightV() {
        setContentPane(contentPane);
        setModal(true);
        //setUndecorated(true);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        briSlider.addChangeListener(_ -> doChange(new float[]{0.0f,2.0f,256f}, briSlider, briLabel, vapi::setBrightness));
        conSlider.addChangeListener(_ -> doChange(new float[]{0.0f,2.0f,256f}, conSlider, conLabel, vapi::setContrast));
        satSlider.addChangeListener(_ -> doChange(new float[]{0.01f,3.0f,256f}, satSlider, satLabel, vapi::setSaturation));
        hueSlider.addChangeListener(_ -> doChange(new float[]{-180f,180f,256f}, hueSlider, hueLabel, vapi::setHue));
        scaleSlider.addChangeListener(_ -> doChange(new float[]{0.1f,2.0f,256f}, scaleSlider, scaleLabel, vapi::setScale));
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void doChange (float[] stp, JSlider slider, JLabel lab, Consumer<Float> func) {
        float v = new Stepper(stp).get(slider.getValue());
        lab.setText(Float.toString(v));
        func.accept(v);
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
