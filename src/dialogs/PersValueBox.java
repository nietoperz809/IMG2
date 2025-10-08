package dialogs;


import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PersValueBox extends JDialog {
    private static final JTextField ftext_ulx = new JTextField();
    private static final JTextField ftext_uly = new JTextField();
    private static final JTextField ftext_urx = new JTextField();
    private static final JTextField ftext_ury = new JTextField();
    private static final JTextField ftext_lrx = new JTextField();
    private static final JTextField ftext_lry = new JTextField();
    private static final JTextField ftext_llx = new JTextField();
    private static final JTextField ftext_lly = new JTextField();

    private PersV pval;

    public PersValueBox(ImageView imv) {
        setLayout(null);
        setTitle("Perspective Params");
        ftext_ulx.setToolTipText("Upper Left X");
        ftext_uly.setToolTipText("Upper Left Y");
        ftext_urx.setToolTipText("Upper Right X");
        ftext_ury.setToolTipText("Upper Right Y");
        ftext_lrx.setToolTipText("Lower Right X");
        ftext_lry.setToolTipText("Lower Right Y");
        ftext_llx.setToolTipText("Lower Left X");
        ftext_lly.setToolTipText("Lower Left Y");

        ftext_ulx.setText("0");
        ftext_uly.setText("0");
        ftext_urx.setText(""+imv.getImage().getWidth());
        ftext_ury.setText("0");

        ftext_lrx.setText(""+imv.getImage().getWidth());
        ftext_lry.setText(""+imv.getImage().getHeight());
        ftext_llx.setText("0");
        ftext_lly.setText(""+imv.getImage().getHeight());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pval = PersV.getValues();
                dispose(); // Releases resources and closes the window
            }
        });

        JLabel lblNewLabel = new JLabel("ulx");
        lblNewLabel.setBounds(10, 10, 55, 15);
        ftext_ulx.setBounds(31, 10, 76, 21);
        add (lblNewLabel);
        add (ftext_ulx);

        JLabel label = new JLabel("uly");
        label.setBounds(10, 38, 55, 15);
        ftext_uly.setBounds(31, 38, 76, 21);
        add (label);
        add (ftext_uly);

        JLabel label_1 = new JLabel("urx");
        label_1.setBounds(10, 65, 55, 15);
        ftext_urx.setBounds(31, 65, 76, 21);
        add (label_1);
        add (ftext_urx);

        JLabel label_2 = new JLabel("ury");
        label_2.setBounds(10, 95, 55, 15);
        ftext_ury.setBounds(31, 95, 76, 21);
        add (label_2);
        add (ftext_ury);

        JLabel label_3 = new JLabel("lrx");
        label_3.setBounds(163, 10, 55, 15);
        ftext_lrx.setBounds(184, 10, 76, 21);
        add (label_3);
        add (ftext_lrx);

        JLabel label_4 = new JLabel("lry");
        label_4.setBounds(163, 38, 55, 15);
        ftext_lry.setBounds(184, 38, 76, 21);
        add (label_4);
        add (ftext_lry);

        JLabel label_5 = new JLabel("llx");
        label_5.setBounds(163, 65, 55, 15);
        ftext_llx.setBounds(184, 65, 76, 21);
        add (label_5);
        add (ftext_llx);

        JLabel label_6 = new JLabel("lly");
        label_6.setBounds(163, 95, 55, 15);
        ftext_lly.setBounds(184, 95, 76, 21);
        add (label_6);
        add (ftext_lly);

        setSize (300, 170);
        setLocationRelativeTo(null);
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
                         double lrx, double lry, double llx, double lly) {
        public static PersV getValues() {
            return new PersV(
                    parse(ftext_ulx), parse(ftext_uly), parse(ftext_urx), parse(ftext_ury),
                    parse(ftext_lrx), parse(ftext_lry), parse(ftext_llx), parse(ftext_lly)
            );
        }
    }

    public static PerspectiveTransform get(ImageView imv) {
        PersValueBox pv = new PersValueBox(imv);
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
