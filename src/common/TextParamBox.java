package common;

import thegrid.ImgPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextParamBox extends JDialog {
    private JPanel contentPane;
    private JTextField textField1;
    private JButton fontButton;
    private JButton colorButton;
    private JButton showButton;
    private JTextField a2020TextField;
    private JTextField a05TextField;
    private JButton buttonOK;

    private static final TextParamBox instance = new TextParamBox();

    Watermark watermark = new Watermark();
    private ImgPanel imgPanel;

    public TextParamBox () {
        setContentPane(contentPane);
        setModal(true);
        fontButton.addActionListener(e -> {
            FontChooser2 fc = new FontChooser2(instance);
            fc.setVisible(true);
            watermark.font = fc.getSelectedFont();
        });

        colorButton.addActionListener(e -> {
            watermark.col = JColorChooser.showDialog(null,
                    "Text Color", null);
        });

        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                watermark.text = textField1.getText();
                watermark.alpha = Float.parseFloat(a05TextField.getText());
                String pt[] = a2020TextField.getText().split(",");
                watermark.pos = new Point (Integer.parseInt(pt[0]), Integer.parseInt(pt[1]));
                imgPanel.setWatermark(watermark);
                //System.out.println(watermark);
                dispose();
            }
        });
    }

    public static void xmain (ImgPanel imgp) {
        instance.imgPanel = imgp;
        instance.pack();
        instance.setVisible(true);
    }
}
