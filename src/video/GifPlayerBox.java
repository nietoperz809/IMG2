package video;

import javax.swing.*;
import java.awt.*;

public class GifPlayerBox {

    public void start (byte[] gifdata) {
        JLabel playerLabel = new JLabel();
        ImageIcon icon = new ImageIcon(gifdata);
        int width = 600;
        int height = 600;
        icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        playerLabel.setIcon(icon);

        JFrame playerFrame;
        playerFrame = new JFrame();
        playerFrame.requestFocus();
        playerFrame.setTitle("GifPlayer");
        playerFrame.setBounds(100, 100, width, height);
        playerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        playerFrame.setLayout(new BorderLayout());
        playerFrame.add(playerLabel, BorderLayout.CENTER); //setContentPane(mpc);
        playerFrame.setVisible(true);
    }
}
