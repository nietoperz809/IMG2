package dialogs;

import common.ImageScaler;
import common.MsgBox;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static common.ImageTools.saveImg2Disk;

public class ImageViewer {
    private final BufferedImage m_image;
    private JPanel panel1;
    private JButton button1;
    private JPanel buttonPanel;
    private JPanel imagePanel;

    public ImageViewer(BufferedImage img) {
        final JFrame frame = new JFrame("PreviewImage");
        m_image = img;
        button1.addActionListener(_ -> {
            String outPath = MsgBox.chooseDir(frame);
            BufferedImage imgsc = ImageScaler.scaleImg(m_image, 3.0, true);
            saveImg2Disk(imgsc, 0, outPath, "preview");
            frame.dispose();
        });
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void xmain(BufferedImage bi) {
        new ImageViewer(bi);
    }

    private void createUIComponents() {
        imagePanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(m_image, 0,0, getWidth(), getHeight(), null);
            }
        };
    }
}
