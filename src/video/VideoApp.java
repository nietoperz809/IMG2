package video;

import common.DeferredFileDeleter;
import common.NumToText;
import common.Sam;
import common.Tools;
import database.DBHandler;
import dialogs.LineInput;
import dialogs.MonitorFrame;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static common.Sam.speak;

public class VideoApp extends JDialog {
    private final List<DBHandler.NameID> entireList = new ArrayList<>();
    public String snapDir = "C:\\Users\\Administrator\\Desktop\\snaps";
    public List<DBHandler.NameID> videoList;
    public List<DBHandler.NameID> gifList;
    public List<DBHandler.NameID> webpList;
    private Frame owner;
    private JPanel contentPane;
    private JButton buttonPlay;
    private JButton buttonCancel;
    private JList<DBHandler.NameID> listControl;
    private JButton deleteButton;
    private JButton exportButton;
    private JButton renameButton;
    private JLabel outputDirLabel;
    private JButton buttonMix;
    private JCheckBox checkBoxAC;
    private JCheckBox checkBoxautoNew;
    private PlayerBox playerBox;
    //private JScrollPane listscroll;
    private JButton filterButton;
    private JButton restoreButton;

    public VideoApp() {
        /* define menu bar */
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem mi1 = new JMenuItem("MemMonitor");
        JMenuItem mi2 = new JMenuItem("End Process");
        mi1.addActionListener(_ -> new MonitorFrame());
        mi2.addActionListener(_ -> Tools.shutdown(this));
        menu.add(mi1);
        menu.add(mi2);
        mb.add(menu);
        setJMenuBar(mb);

        outputDirLabel.setText(snapDir);
        outputDirLabel.setToolTipText("Output Dir, klick to change ...");
        outputDirLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                snapDir = Tools.chooseDir(VideoApp.this);
                if (snapDir == null)
                    snapDir = System.getProperty("java.io.tmpdir");
                snapDir += File.separator;
                outputDirLabel.setText(snapDir);
                repaint();
            }
        });
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonPlay);

        buttonPlay.addActionListener(_ -> CancelOldAndPlayNew());

        buttonCancel.addActionListener(_ -> {
            checkBoxautoNew.setSelected(false);  // stop video show
            onCancel();
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (owner != null)
                    owner.setVisible(true);
                onCancel();
            }
        });

        setAndSortJListContent();

        enableDrop();

        listControl.ensureIndexIsVisible(listControl.getSelectedIndex());
        listControl.setToolTipText("right click to get BLOB size, be patient");

        deleteButton.addActionListener(_ -> {
            DBHandler.NameID nameid = listControl.getSelectedValue();
            if (!Tools.Question("Really delete " + nameid.name() + "?")) {
                return;
            }
            if (gifList.contains(nameid)) {
                DBHandler.deleteGif(nameid.rowid());
            } else if (webpList.contains(nameid)) {
                DBHandler.deleteWEBP(nameid.rowid());
            } else {
                DBHandler.deleteVideo(nameid.rowid());
            }
            setAndSortJListContent();
            repaint();
        });

        exportButton.addActionListener(_ -> {
            final List<DBHandler.NameID> selectedValuesList = listControl.getSelectedValuesList();
            if (selectedValuesList.size() == 1) {
                saveSingle (selectedValuesList.getFirst());
            }
            else {
                saveMulti (selectedValuesList);
            }
        });

        renameButton.addActionListener(_ -> {
            DBHandler.NameID nameid = listControl.getSelectedValue();
            String res = LineInput.xmain(nameid.name(), "NewName", Color.orange);
            if (res.isEmpty())
                return;
            if (gifList.contains(nameid)) {
                DBHandler.changeGifName(res, nameid.rowid());
            } else if (webpList.contains(nameid)) {
                DBHandler.changeWebpName(res, nameid.rowid());
            } else {
                DBHandler.changeVideoName(res, nameid.rowid());
            }
            setAndSortJListContent();
        });

        listControl.setCellRenderer(new MyCellRenderer(this));

        buttonMix.addActionListener(_ -> mix());

        filterButton.addActionListener(_ -> {
            String input = Tools.getInput("search for ...");
            if (input == null || input.isEmpty())
                return;
            input = input.toLowerCase();
            ArrayList<DBHandler.NameID> filteredList = new ArrayList<>();
            for (DBHandler.NameID nid : entireList) {
                if (nid.name().toLowerCase().contains(input)) {
                    filteredList.add(nid);
                }
            }
            listToListControl(filteredList);
        });

        restoreButton.addActionListener(_ -> listToListControl(entireList));

        /*
         * Right mouseclick on listcontrol
         */
        listControl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = listControl.locationToIndex(e.getPoint());
                    listControl.setSelectedIndex(row);
                    DBHandler.NameID nid = listControl.getSelectedValue();
                    String len = null;
                    if (videoList.contains(nid))
                        len = DBHandler.getVideoBlobLen(nid);
                    else if (gifList.contains(nid))
                        len = DBHandler.getGifBlobLen(nid);
                    else if (webpList.contains(nid))
                        len = DBHandler.getWEBPBlobLen(nid);
                    Sam.speak(NumToText.convert(len) + " Bites");
                    String flen = NumberFormat.getNumberInstance(Locale.GERMAN)
                            .format(Double.parseDouble(len));
                    Tools.Info("Bloblen: " + flen + " Bytes");
                }
            }
        });
    }

    private SoftReference<byte[]> getVideoBytes (DBHandler.NameID nameid) {
        try {
            if (gifList.contains(nameid)) {
                return DBHandler.loadGifBytes(nameid);
            } else if (webpList.contains(nameid)) {
                return DBHandler.loadWEBPBytes(nameid);
            }
            return DBHandler.loadVideoBytes(nameid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveMulti (List<DBHandler.NameID> selectedValuesList) {
        ZipParameters zipParameters = Tools.getStandardZipParams();
        String outPath = Tools.chooseDir(this);
        System.out.println(outPath);
        ZipFile zipFile = new ZipFile (outPath + File.separator +
                System.currentTimeMillis()+"-animations.rar",
                "imagebase".toCharArray());
        //zipFile.
        String tmpdir = System.getProperty("java.io.tmpdir");
        try {
            for (DBHandler.NameID nid : selectedValuesList) {
                String filename = tmpdir + File.separator+nid.name();
                if (videoList.contains(nid)) {
                    if (!filename.endsWith(".mp4)")) {
                        filename = filename + ".mp4";
                    }
                }
                SoftReference<byte[]> bt = getVideoBytes (nid);
                Files.write (Path.of(filename), bt.get());
                zipFile.addFile(filename, zipParameters);
                DeferredFileDeleter.put (filename);
            }
            zipFile.close();
            Sam.speak("ZIP file created!");
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    private void saveSingle (DBHandler.NameID nameid) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(nameid.name()));
            int option = fileChooser.showSaveDialog(VideoApp.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                SoftReference<byte[]> bt = getVideoBytes (nameid);
                Files.write(f.toPath(), bt.get());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Start video app
     *
     * @param owner The caller window
     */
    public static void open(Frame owner) {
        VideoApp dialog = new VideoApp();
        dialog.owner = owner;
        if (owner != null)
            owner.setVisible(false);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * select next vid in list
     */
    private void selectNextVid() {
        int idx = listControl.getSelectedIndex() + 1;
        if (idx >= listControl.getModel().getSize())
            idx = 0;
        listControl.setSelectedIndex(idx);
    }

    /**
     * Play new vid, dismiss the old one
     */
    private void CancelOldAndPlayNew() {
        onCancel();
        onOK();
    }

    private void onOK() {
        SwingUtilities.invokeLater(this::transferAndRun);
        //transferAndRun();
        //Tools.runTask(() -> transferAndRun());
    }

    /**
     * Start playing
     */
    private void transferAndRun() {
        DBHandler.NameID nid = listControl.getSelectedValue();
        if (nid == null) {
            listControl.setSelectedIndex(0);
            nid = listControl.getSelectedValue();
        }
        if (gifList.contains(nid)) try {
            File f = DBHandler.transferGifIntoFile(nid);
            playerBox = new AnimPlayerBox(f, this,
                    new GifDecoder(), checkBoxAC.isSelected());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        else if (webpList.contains(nid)) try {
            File f = DBHandler.transferwEBPIntoFile(nid);
            playerBox = new AnimPlayerBox(f, this,
                    new WebPDecoder(), checkBoxAC.isSelected());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        else playerBox = new VideoPlayerBox(this, nid, checkBoxAC.isSelected());
        playerBox.start();
    }

    /**
     * if client closed
     */
    public void clientDisposed() {
        if (checkBoxautoNew.isSelected()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            selectNextVid();
            onOK();
        }
    }

    private void onCancel() {
        if (playerBox == null)
            return;
        DBHandler.cancelFileTransfer();
        playerBox.stop();
        DBHandler.cancelFileTransfer();
    }

    private void listToListControl(List<DBHandler.NameID> list) {
        DefaultListModel lm = new DefaultListModel<>();
        lm.addAll(list);
        listControl.setModel(lm);
        listControl.repaint();
    }

    /**
     * Initial filling the JList
     */
    private void setAndSortJListContent() {
        entireList.clear();
        videoList = DBHandler.getVideoFileNames();
        //------------------
        //String test = DBHandler.getVideoBlobLen(videoList.get(0));
        //------------------
        gifList = DBHandler.getGifFileNames();
        webpList = DBHandler.getWebPFileNames();
        entireList.addAll(videoList);
        entireList.addAll(gifList);
        entireList.addAll(webpList);
        listToListControl(entireList);
    }

    /**
     * Random shuffle the list of available vids/anims
     */
    private void mix() {
        List<DBHandler.NameID> mixedList = new ArrayList<>();
        mixedList.addAll(videoList);
        mixedList.addAll(gifList);
        mixedList.addAll(webpList);
        Collections.shuffle(mixedList);
        listToListControl(mixedList);
    }

    /**
     * DragDrop on Jlist,
     */
    private void enableDrop() {
        new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = event.getTransferable();
                DataFlavor[] flavors = transferable.getTransferDataFlavors();
                for (DataFlavor flavor : flavors) {
                    if (flavor.isFlavorJavaFileListType()) {
                        try {
                            java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(flavor);
                            for (File f : files) {
                                if (Tools.isGIF(f.getPath())) {
                                    DBHandler.addGifFile(f);
                                    speak("GIF file added");
                                } else if (Tools.isWEBP(f.getPath())) {
                                    DBHandler.addWebPFile(f);
                                    speak("WEBP file added");
                                } else {
                                    DBHandler.addVideoFile(f);
                                    speak("Regular video added");
                                }
                                DeferredFileDeleter.put(f);
//                                if (!f.delete()) {
//                                    speak("could not delete");
//                                }
                            }
                            setAndSortJListContent();
                            repaint();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

    /**
     *
     */
    static class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {

        final VideoApp m_va;

        MyCellRenderer(VideoApp va) {
            m_va = va;
        }

        public Component getListCellRendererComponent(
                JList<?> list,           // the list
                Object value,            // value to display
                int index,               // cell index
                boolean isSelected,      // is the cell selected
                boolean cellHasFocus)    // does the cell have focus
        {
            String s = value.toString();
            if (m_va.gifList.contains(value)) {
                setForeground(Color.RED);
            } else if (m_va.webpList.contains(value)) {
                setForeground(Color.BLUE);
            } else {
                setForeground(Color.BLACK);
            }

            if (isSelected) {
                setBackground(Color.YELLOW);
            } else {
                setBackground(Color.WHITE);
            }
            setText(s);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

//    public static void main(String[] args) {
//        VideoApp dialog = new VideoApp();
//        dialog.setSize(400,400);
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);
//    }
}
