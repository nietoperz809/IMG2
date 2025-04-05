package dialogs;

import Catalano.Imaging.FastBitmap;
import common.ImgTools;
import thegrid.ImgPanel;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.function.BiFunction;

public class RGBScroll extends JDialog {
    private JPanel contentPane;
    private JSlider sliderRed;
    private JSlider sliderGreen;
    private JSlider sliderBlue;
    private JSlider sliderAlpha;
    private JRadioButton rbAnd;
    private JRadioButton rbOr;
    private JRadioButton rbXor;
    private JRadioButton rbPlus;
    private ButtonGroup buttonGroup1;
    private FastBitmap image;
    private FastBitmap copy;
    private ImgPanel panel;
    private BiFunction<Integer, Integer, Integer> func;

    public void setImage (BufferedImage img, ImgPanel imgPanel) {
        panel = imgPanel;
        image = new FastBitmap(img);
        copy = new FastBitmap(img);
    }

    public RGBScroll() {
        setContentPane(contentPane);
        setModal(true);
        setUndecorated(true);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(_-> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        sliderRed.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int v = sliderRed.getValue();
                for (int s=0; s<image.getRGBData().length; s++)
                    image.setRed (s, func.apply (v,copy.getRed(s)));
                panel.setImage(image);
            }
        });

        sliderGreen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int v = sliderGreen.getValue();
                for (int s=0; s<image.getRGBData().length; s++)
                    image.setGreen (s, func.apply (v,copy.getGreen(s)));
                panel.setImage(image);
            }
        });

        sliderBlue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int v = sliderBlue.getValue();
                for (int s=0; s<image.getRGBData().length; s++)
                    image.setBlue (s, v^copy.getBlue(s));
                panel.setImage(image);
            }
        });

        sliderAlpha.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int v = sliderAlpha.getValue();
                float val = 2f*v/255;
                BufferedImage buf = image.toBufferedImage();
                buf = ImgTools.contrast(buf, val);
                panel.setImage(buf);
            }
        });

        rbAnd.addActionListener(_ -> func = (x1, x2) -> x1 & x2);
        rbOr.addActionListener(_ -> func = (x1, x2) -> x1 | x2);
        rbXor.addActionListener(_ -> func = (x1, x2) -> x1 ^ x2);
        rbPlus.addActionListener(_ -> func = Integer::sum);

        func = Integer::sum;
        rbPlus.setSelected(true);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void xmain(BufferedImage img, ImgPanel imgPanel) {
        RGBScroll dialog = new RGBScroll();
        dialog.setImage(img, imgPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        RGBScroll dialog = new RGBScroll();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        System.exit(0);
    }
}
