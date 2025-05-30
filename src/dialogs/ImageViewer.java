package dialogs;

import common.MsgBox;
import thegrid.gridmenu.SubMenuMarked;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import static common.ImgTools.saveImg2Disk;

public class ImageViewer {
    private final BufferedImage m_image;
    private JPanel panel1;
    private JButton button1;
    private JPanel buttonPanel;
    private JPanel imagePanel;

    public ImageViewer(BufferedImage img) {
        m_image = img;
        button1.addActionListener(_ -> {
            String outPath = MsgBox.chooseDir(button1);
            saveImg2Disk(m_image, 0,outPath);
        });
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("ImageViewer");
//        frame.setContentPane(new ImageViewer(null).panel1);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }

    public static void xmain(BufferedImage bi) {
        JFrame frame = new JFrame("PreviewImage");
        ImageViewer imv = new ImageViewer(bi);
        frame.setContentPane(imv.panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
