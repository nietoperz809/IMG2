package dialogs.video;

import common.Stepper;
import org.jetbrains.annotations.NotNull;
import uk.co.caprica.vlcj.player.base.VideoApi;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

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
    private JButton resetButton;
    private final VideoApi vapi;

    public ConBrightV(@NotNull VideoApi v) {
        vapi = v;
        vapi.setAdjustVideo(true);
        setContentPane(contentPane);
        setModal(true);

        setDefaultCloseOperation (DISPOSE_ON_CLOSE);
        contentPane.registerKeyboardAction(_ -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        briSlider.addChangeListener(_ -> doChange(new float[]{0.0f,2.0f,256f}, briSlider, briLabel, vapi::setBrightness));
        conSlider.addChangeListener(_ -> doChange(new float[]{0.0f,2.0f,256f}, conSlider, conLabel, vapi::setContrast));
        satSlider.addChangeListener(_ -> doChange(new float[]{0.01f,3.0f,256f}, satSlider, satLabel, vapi::setSaturation));
        hueSlider.addChangeListener(_ -> doChange(new float[]{-180f,180f,256f}, hueSlider, hueLabel, vapi::setHue));
        scaleSlider.addChangeListener(_ -> doChange(new float[]{0.1f,2.0f,256f}, scaleSlider, scaleLabel, vapi::setScale));

        resetButton.addActionListener(_ -> reset());
    }

    private void reset() {
        briSlider.setValue (100);
        conSlider.setValue (100);
        satSlider.setValue (100);
        hueSlider.setValue (100);
        scaleSlider.setValue (100);
    }

    private void doChange (float[] stp, JSlider slider, JLabel lab, Consumer<Float> func) {
        try {
            float v = new Stepper(stp).get(slider.getValue());
            lab.setText(Float.toString(v));
            func.accept(v);
        } catch (Exception e) {
            System.out.println("fuck");
        }
    }

    public static void popup(@NotNull VideoApi v) {
        ConBrightV dialog = new ConBrightV(v);
        dialog.pack();
        dialog.reset();
        dialog.setVisible(true);
    }
}
