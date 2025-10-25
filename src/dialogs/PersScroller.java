package dialogs;

import javafx.scene.effect.PerspectiveTransform;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    private static JfxImageView m_host = null;

    public PersScroller(JfxImageView host) {
        if (host.m_scrollfield != null)
        {
            dispose();
            host.m_scrollfield = null;
            return;
        }
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                m_host.m_scrollfield = null;
                m_host = null;
            }
        });
        m_host = host;
        setContentPane(contentPane);
        setModal(false);
        getRootPane().setDefaultButton(buttonOK);
        setResizable(false);
        setLocationRelativeTo(null);
        //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        sl_ulx.addChangeListener(e -> doForSlider(e,"ulx"));
        sl_uly.addChangeListener(e -> doForSlider(e,"uly"));
        sl_urx.addChangeListener(e -> doForSlider(e,"urx"));
        sl_ury.addChangeListener(e -> doForSlider(e,"ury"));
        sl_lrx.addChangeListener(e -> doForSlider(e,"lrx"));
        sl_lry.addChangeListener(e -> doForSlider(e,"lry"));
        sl_llx.addChangeListener(e -> doForSlider(e,"llx"));
        sl_lly.addChangeListener(e -> doForSlider(e,"lly"));
        sl_ulx.setValue(0);
        sl_uly.setValue(0);
        sl_urx.setValue((int)host.imgV.getImage().getWidth());
        sl_ury.setValue(0);
        sl_lrx.setValue((int)host.imgV.getImage().getWidth());
        sl_lry.setValue((int)host.imgV.getImage().getHeight());
        sl_llx.setValue(0);
        sl_lly.setValue((int)host.imgV.getImage().getHeight());
        pack();
        setVisible(true);
    }

    private void doForSlider(ChangeEvent e, String prefix) {
        JSlider sl = (JSlider) e.getSource();
        sl.setToolTipText(prefix+":"+sl.getValue());
        m_pt.setUlx(sl_ulx.getValue());
        m_pt.setUly(sl_uly.getValue());
        m_pt.setUrx(sl_urx.getValue());
        m_pt.setUry(sl_ury.getValue());
        m_pt.setLrx(sl_lrx.getValue());
        m_pt.setLry(sl_lry.getValue());
        m_pt.setLlx(sl_llx.getValue());
        m_pt.setLly(sl_lly.getValue());
        m_host.setEffect(m_pt);
    }

    public static void main(String[] args) {
        PersScroller dialog = new PersScroller(null);
        System.exit(0);
    }

    public static PersScroller start(JfxImageView host) {
        PersScroller dialog = new PersScroller(host);
        return dialog;
    }

}
