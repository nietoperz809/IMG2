package thegrid;

import imageloader.ImageStore;
import imageloader.SQLReader;
import imageloader.Zipper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;


public class TheGrid extends JFrame {
    private static final NPExecutor pool = new NPExecutor(20, 10000);
    public final String srcDir;
    private final JPanel rootPane;
    private final JScrollPane scrollPane;
    private final java.util.List<String> allFiles;
    private final ProgressBox progress;
    private final SQLReader imageStore;
    private final java.util.List<MemoryFile> newThumbs = new CopyOnWriteArrayList<>();
    private Instant startTime;
    private int imageCount;

    public TheGrid() throws Exception {
        Object[] dirs = Tools.getDirs();
        srcDir = (String) dirs[0];

        imageStore = new SQLReader("jdbc:h2:./mydb");

        allFiles = imageStore.getFileNames(); //Tools.listImages(srcDir);

        progress = new ProgressBox(this, allFiles.size());

        rootPane = new JPanel();
        scrollPane = new JScrollPane(rootPane);
        rootPane.setLayout(new GridLayout(0, 10, 2, 2));
        add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenuItem menu = new JMenuItem("Directory");
        menu.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(this)) {
                ((PersistString) dirs[2]).set(jfc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(null, "Please restart the app ...");
                System.exit(0);
            }
        });
        menuBar.add(menu);
        setJMenuBar(menuBar);

        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void run() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            TheGrid tg = new TheGrid();
            tg.imageCount = 0;
            tg.newThumbs.clear();
            tg.startTime = Instant.now();

            //for (int s = 0; s < 10; s++) {
            for (int s = 0; s < tg.allFiles.size(); s++) {
                tg.addImg(s);
            }
        } catch (Exception e) {
            System.err.println("run fail\n" + e);
        }
    }

    public static void main(String... ignored) {
        run();
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
//        if (thumbnailImage == null) {
//            try {
//                synchronized (this) {
//                    thumbnailImage = ImageScaler.scaleExact(imageStore.loadImage(fileName),
//                            new Dimension(100, 100));
//                    newThumbs.add(new MemoryFile(thumbnailName, thumbnailImage));
//                }
//            } catch (Exception ex) {
//                System.err.println("Thumb creation fail for: " + fileName);
//                try {
//                    thumbnailImage = ImageIO.read(Objects.requireNonNull(Tools.getResource("fail.png")));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
        GridImage lab = new GridImage(thumbnailImage, allFiles, s, imageStore);

        rootPane.add(lab);
        Instant end = Instant.now();
        String txt = "Loaded " + (++imageCount) + " Thumbs in " + Duration.between(startTime, end).toMillis() / 1000 + " Seconds";
        setTitle(txt);
        progress.setTextVal(txt, imageCount);
        if (imageCount >= allFiles.size()) {
            if (newThumbs.size() > 0) synchronized (this) {

                // TODO: needs update                try {
//                    thumbStore.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                Zipper.addFilesToZip(new File(thumbStore.getSource()), newThumbs);
//                newThumbs.clear();
//                System.out.println("addzip");
            }
            progress.dispose();
            rootPane.doLayout();
            scrollPane.getViewport().setView(rootPane);
        }
    }
}


