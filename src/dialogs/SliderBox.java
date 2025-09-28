package dialogs;

import common.Stepper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.text.DecimalFormat;

public class SliderBox extends JDialog {
    private JPanel contentPane;
    private JSlider theSlider;
    private JLabel valueLabel;
    private final Stepper stepper; // = new Stepper (0.4f, 2.0f, 255);
    private float lastStep = 1.0f;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public SliderBox(Stepper stp) {
        stepper = stp;
        theSlider.setValue(stp.getSteps()/2);
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
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        theSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                dispose();
            }
        });

        theSlider.addChangeListener(_ -> {
            lastStep = stepper.get(theSlider.getValue());
            valueLabel.setText(df.format(lastStep));
        });
    }

    private void onCancel() {
        //throw new RuntimeException("break");
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
//        SliderBox dialog = new SliderBox(null);
//        dialog.pack();
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);
//        System.exit(0);
    }

    public static float xmain (String name, float from, float to, int steps) {
        SliderBox dialog = new SliderBox(new Stepper(from, to, steps));
        dialog.setTitle(name);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.lastStep;
    }

}
