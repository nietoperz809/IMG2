package thegrid;

import imageloader.DBHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.prefs.Preferences;


public class TheGrid extends JFrame {
    //public final String srcDir;
    private final JPanel rootPane;
    private final JScrollPane scrollPane;
    private final java.util.List<String> allFiles;
    private final ProgressBox progress;
    private final DBHandler imageStore;
    private Instant startTime;
    private int imageCount;

    public TheGrid() {

        imageStore = new DBHandler();

        allFiles = imageStore.getFileNames();

        progress = new ProgressBox(this, allFiles.size());

        rootPane = new JPanel();
        scrollPane = new JScrollPane(rootPane);
        rootPane.setLayout(new GridLayout(0, 10, 2, 2));
        add(scrollPane);

        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        rootPane.setToolTipText(allFiles.size() + " Images, press 'a' to add more");
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    String lastDirectory = Preferences.userNodeForPackage(rootPane.getClass()).get("Images.lastDirectory", System.getProperty("user.home"));
                    JFileChooser fc = new JFileChooser();
                    File lastPath = new File(lastDirectory);
                    if (lastPath.exists() && lastPath.isDirectory()) {
                        fc.setCurrentDirectory(new File(lastDirectory));
                    }
                    FileNameExtensionFilter filt = new FileNameExtensionFilter("Image Files", Tools.getImageExtensions());
                    fc.setFileFilter(filt);
                    fc.setMultiSelectionEnabled(true);
                    if (fc.showOpenDialog(rootPane) == JFileChooser.APPROVE_OPTION) {
                        lastDirectory = fc.getCurrentDirectory().getPath();
                        Preferences.userNodeForPackage(rootPane.getClass()).put("Images.lastDirectory", lastDirectory);
                        File[] files = fc.getSelectedFiles();
                        try {
                            imageStore.addImages(files, (img, name) -> {
                                BufferedImage thumbnailImage = ImageScaler.scaleExact(img,
                                        new Dimension(100, 100));
                                GridImage lab = new GridImage(thumbnailImage, allFiles, -1, imageStore, rootPane);
                                lab.setName (name);
                                rootPane.add(lab);
                            });
                            rootPane.doLayout();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                imageStore.close();
                System.exit(1);
            }
        });

        setVisible(true);
    }


    public static void main(String... ignored) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            TheGrid tg = new TheGrid();
            tg.imageCount = 0;
            tg.startTime = Instant.now();

            //for (int s = 0; s < 10; s++) {
            for (int s = 0; s < tg.allFiles.size(); s++) {
                tg.addImg(s);
            }
        } catch (Exception e) {
            System.err.println("run fail\n" + e);
        }
    }

    /**
     * Add one single image to the frame
     */
    public void addImg(int s) {
        String fileName = allFiles.get(s);
        //String thumbnailName = fileName + ".jpg";
        BufferedImage thumbnailImage = null;
        try {
            thumbnailImage = imageStore.loadThumbnail(fileName);
        } catch (Exception e) {
            System.err.println("thumb read fail: " + fileName);
        }
        GridImage lab = new GridImage(thumbnailImage, allFiles, s, imageStore, rootPane);

        rootPane.add(lab);
        Instant end = Instant.now();
        String txt = "Loaded " + (++imageCount) + " Thumbs in " + Duration.between(startTime, end).toMillis() / 1000 + " Seconds";
        setTitle(txt);
        progress.setTextAndValue(txt, imageCount);
        if (imageCount >= allFiles.size()) {
            progress.dispose();
            rootPane.doLayout();
            scrollPane.getViewport().setView(rootPane);
        }
    }
}


