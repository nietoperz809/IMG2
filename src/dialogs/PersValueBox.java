package dialogs;


import javafx.scene.effect.PerspectiveTransform;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PersValueBox extends JDialog {
    private static final JTextField ftext_0 = new JTextField();
    private static final JTextField ftext_1 = new JTextField();
    private static final JTextField ftext_2 = new JTextField();
    private static final JTextField ftext_3 = new JTextField();
    private static final JTextField ftext_4 = new JTextField();
    private static final JTextField ftext_5 = new JTextField();
    private static final JTextField ftext_6 = new JTextField();
    private static final JTextField ftext_7= new JTextField();

    private PersV pval;

    public PersValueBox() {
        setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pval = PersV.getValues();
                dispose(); // Releases resources and closes the window
            }
        });

        JLabel lblNewLabel = new JLabel("ulx");
        lblNewLabel.setBounds(10, 10, 55, 15);
        ftext_0.setBounds(31, 10, 76, 21);
        add (lblNewLabel);
        add (ftext_0);

        JLabel label = new JLabel("uly");
        label.setBounds(10, 38, 55, 15);
        ftext_1.setBounds(31, 38, 76, 21);
        add (label);
        add (ftext_1);

        JLabel label_1 = new JLabel("urx");
        label_1.setBounds(10, 65, 55, 15);
        ftext_2.setBounds(31, 65, 76, 21);
        add (label_1);
        add (ftext_2);

        JLabel label_2 = new JLabel("ury");
        label_2.setBounds(10, 95, 55, 15);
        ftext_3.setBounds(31, 95, 76, 21);
        add (label_2);
        add (ftext_3);

        JLabel label_3 = new JLabel("lrx");
        label_3.setBounds(163, 10, 55, 15);
        ftext_4.setBounds(184, 10, 76, 21);
        add (label_3);
        add (ftext_4);

        JLabel label_4 = new JLabel("lry");
        label_4.setBounds(163, 38, 55, 15);
        ftext_5.setBounds(184, 38, 76, 21);
        add (label_4);
        add (ftext_5);

        JLabel label_5 = new JLabel("llx");
        label_5.setBounds(163, 65, 55, 15);
        ftext_6.setBounds(184, 65, 76, 21);
        add (label_5);
        add (ftext_6);

        JLabel label_6 = new JLabel("lly");
        label_6.setBounds(163, 95, 55, 15);
        ftext_7.setBounds(184, 95, 76, 21);
        add (label_6);
        add (ftext_7);

        setSize (300, 170);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setModal(true);
        setVisible(true);
    }

    static double parse (JTextField tf) {
        String s = tf.getText();
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public record PersV (double ulx, double uly, double urx, double ury,
                         double llx, double lly, double lrx, double lry) {
        public static PersV getValues() {
            return new PersV(
                    parse(ftext_0), parse(ftext_1), parse(ftext_2), parse(ftext_3),
                    parse(ftext_4), parse(ftext_5), parse(ftext_6), parse(ftext_7)
            );
        }
    }

    public static PerspectiveTransform get() {
        PersValueBox pv = new PersValueBox();
        return new PerspectiveTransform(pv.pval.ulx, pv.pval.uly, pv.pval.urx, pv.pval.ury,
                pv.pval.lrx, pv.pval.lry, pv.pval.llx, pv.pval.lly);
    }

    /*
    public static void main(String[] args) {
        PersValues pv = new PersValues();
        pv.setVisible(true);
        try {
            pv.latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(pv.pval);
    }
    */
}
