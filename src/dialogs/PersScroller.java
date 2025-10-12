package dialogs;

import javafx.scene.effect.PerspectiveTransform;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class PersScroller extends JDialog {
    private JPanel contentPane;
    private JSlider sl_ulx;
    private JSlider sl_uly;
    private JSlider sl_urx;
    private JSlider sl_ury;
    private JSlider sl_lrx;
    private JSlider sl_lry;
    private JSlider sl_llx;
    private JSlider sl_lly;
    private JButton buttonOK;
    private final PerspectiveTransform m_pt = new PerspectiveTransform();
    private JfxImageView host;

    public PersScroller(JfxImageView host) {
        this.host = host;
        setContentPane(contentPane);
        setModal(false);
        getRootPane().setDefaultButton(buttonOK);
        setResizable(false);
        setLocationRelativeTo(null);
        //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        sl_ulx.addChangeListener(this::doForSlider);
        sl_uly.addChangeListener(this::doForSlider);
        sl_urx.addChangeListener(this::doForSlider);
        sl_ury.addChangeListener(this::doForSlider);
        sl_lrx.addChangeListener(this::doForSlider);
        sl_lry.addChangeListener(this::doForSlider);
        sl_llx.addChangeListener(this::doForSlider);
        sl_lly.addChangeListener(this::doForSlider);

        //------------------
        sl_ulx.setValue(0);
        sl_uly.setValue(0);
        sl_urx.setValue((int)host.imgV.getImage().getWidth());
        sl_ury.setValue(0);
        sl_lrx.setValue((int)host.imgV.getImage().getWidth());
        sl_lry.setValue((int)host.imgV.getImage().getHeight());
        sl_llx.setValue(0);
        sl_lly.setValue((int)host.imgV.getImage().getHeight());
        //------------------

        pack();
        setVisible(true);
    }

    private void doForSlider(ChangeEvent e) {
        JSlider sl = (JSlider) e.getSource();
        sl.setToolTipText(""+sl.getValue());
        m_pt.setUlx(sl_ulx.getValue());
        m_pt.setUly(sl_uly.getValue());
        m_pt.setUrx(sl_urx.getValue());
        m_pt.setUry(sl_ury.getValue());
        m_pt.setLrx(sl_lrx.getValue());
        m_pt.setLry(sl_lry.getValue());
        m_pt.setLlx(sl_llx.getValue());
        m_pt.setLly(sl_lly.getValue());
        host.setEffect(m_pt);
    }

    public static void main(String[] args) {
        PersScroller dialog = new PersScroller(null);
        System.exit(0);
    }

    public static void start(JfxImageView host) {
        PersScroller dialog = new PersScroller(host);
    }

}
