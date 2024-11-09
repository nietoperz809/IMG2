package video;

import common.NumToText;
import common.Sam;
import dialogs.LineInput;
import common.Tools;
import database.DBHandler;
import dialogs.MonitorFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static common.Sam.speak;

public class VideoApp extends JDialog {
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
    public String snapDir = "C:\\Users\\Administrator\\Desktop\\snaps";
    private PlayerBox playerBox;
    public List<DBHandler.NameID> videoList;
    public List<DBHandler.NameID> gifList;
    public List<DBHandler.NameID> webpList;
    private final List<DBHandler.NameID> entireList = new ArrayList<>();
    //private JScrollPane listscroll;
    private JButton filterButton;
    private JButton restoreButton;

    public VideoApp () {
        /* define menu bar */
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem mi1 = new JMenuItem("MemMonitor");
        JMenuItem mi2 = new JMenuItem("End Process");
        mi1.addActionListener(e -> new MonitorFrame());
        mi2.addActionListener(e -> System.exit(0));
        menu.add(mi1);
        menu.add(mi2);
        mb.add(menu);
        setJMenuBar(mb);
        
        outputDirLabel.setText (snapDir);
        outputDirLabel.setToolTipText("Output Dir, klick to change ...");
        outputDirLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                snapDir = Tools.chooseDir(VideoApp.this)+File.separator;
                outputDirLabel.setText(snapDir);
                repaint();
            }
        });
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonPlay);

        buttonPlay.addActionListener(e -> CancelOldAndPlayNew());

        buttonCancel.addActionListener(e -> {
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
//        setResizable(false);
//        setSize(800, 600);

        listControl.ensureIndexIsVisible(listControl.getSelectedIndex());
        listControl.setToolTipText("right click to get BLOB size, be patient");

        deleteButton.addActionListener(e -> {
            DBHandler.NameID nameid = listControl.getSelectedValue();
            if (!Tools.Question("Really delete "+nameid.name()+"?")) {
                return;
            }
            if (gifList.contains(nameid)) {
                DBHandler.getInst().deleteGif(nameid.rowid());
            } else if (webpList.contains(nameid)) {
                DBHandler.getInst().deleteWEBP(nameid.rowid());
            } else {
                DBHandler.getInst().deleteVideo(nameid.rowid());
            }
            setAndSortJListContent();
            repaint();
        });

        exportButton.addActionListener(actionEvent -> {
            DBHandler.NameID nameid = listControl.getSelectedValue();
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(nameid.name()));
                int option = fileChooser.showSaveDialog(VideoApp.this);
                if(option == JFileChooser.APPROVE_OPTION){
                    File f = fileChooser.getSelectedFile();
                    SoftReference<byte[]> bt;
                    if (gifList.contains(nameid)) {
                        bt = DBHandler.getInst().loadGifBytes(nameid);
                    } else if (webpList.contains(nameid)) {
                        bt = DBHandler.getInst().loadWEBPBytes(nameid);
                    }
                    else {
                        bt = DBHandler.getInst().loadVideoBytes(nameid);
                    }
                    Files.write(f.toPath(), bt.get());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        renameButton.addActionListener(actionEvent -> {
            DBHandler.NameID nameid = listControl.getSelectedValue();
            String res = LineInput.xmain(nameid.name(), "NewName", Color.orange);
            if (res.isEmpty())
                return;
            if (gifList.contains(nameid)) {
                DBHandler.getInst().changeGifName(res, nameid.rowid());
            } else if (webpList.contains(nameid)) {
                DBHandler.getInst().changeWebpName(res, nameid.rowid());
            } else {
                DBHandler.getInst().changeVideoName(res, nameid.rowid());
            }
            setAndSortJListContent();
        });

        listControl.setCellRenderer(new MyCellRenderer(this));

        buttonMix.addActionListener(e -> mix());

        filterButton.addActionListener(e -> {
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

        restoreButton.addActionListener(e -> listToListControl(entireList));

        /*
         * Right mouseclick on listcontrol
         */
        listControl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ( SwingUtilities.isRightMouseButton(e) ) {
                    int row = listControl.locationToIndex(e.getPoint());
                    listControl.setSelectedIndex(row);
                    DBHandler.NameID nid = listControl.getSelectedValue();
                    String len = null;
                    if (videoList.contains(nid))
                        len = DBHandler.getInst().getVideoBlobLen(nid);
                    else if (gifList.contains(nid))
                        len = DBHandler.getInst().getGifBlobLen(nid);
                    else if (webpList.contains(nid))
                        len = DBHandler.getInst().getWEBPBlobLen(nid);
                    Sam.speak(NumToText.convert(len) + " Bites");
                    String flen = NumberFormat.getNumberInstance(Locale.GERMAN)
                            .format(Double.parseDouble(len));
                    Tools.Info("Bloblen: "+flen+ " Bytes");
                }
            }
        });
    }


    /**
     * select next vid in list
     */
    private void selectNextVid() {
        int idx = listControl.getSelectedIndex()+1;
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
        SwingUtilities.invokeLater(() -> transferAndRun());
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
            File f = DBHandler.getInst().transferGifIntoFile(nid);
            playerBox = new AnimPlayerBox(f, this,
                    new GifDecoder(), checkBoxAC.isSelected());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        else if (webpList.contains(nid)) try {
            File f = DBHandler.getInst().transferwEBPIntoFile(nid);
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
        DBHandler.getInst().cancelFileTransfer();
        playerBox.stop();
        DBHandler.getInst().cancelFileTransfer();
    }

    /**
     *
     */
    static class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {

        VideoApp m_va;
        MyCellRenderer (VideoApp va) {
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
        videoList = DBHandler.getInst().getVideoFileNames();
        //------------------
        //String test = DBHandler.getInst().getVideoBlobLen(videoList.get(0));
        //------------------
        gifList = DBHandler.getInst().getGifFileNames();
        webpList = DBHandler.getInst().getWebPFileNames();
        entireList.addAll (videoList);
        entireList.addAll (gifList);
        entireList.addAll (webpList);
        listToListControl (entireList);
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
                            for(File f : files) {
                                if (Tools.isGIF(f.getPath())) {
                                    DBHandler.getInst().addGifFile(f);
                                    speak("GIF file added");
                                } else if (Tools.isWEBP(f.getPath())) {
                                    DBHandler.getInst().addWebPFile(f);
                                    speak("WEBP file added");
                                } else {
                                    DBHandler.getInst().addVideoFile(f);
                                    speak("Regular video added");
                                }
                                if (!f.delete()) {
                                    speak ("could not delete");
                                }
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
     * Start video app
     * @param owner The caller window
     */
    public static void open(Frame owner) {
        VideoApp dialog = new VideoApp();
        dialog.owner = owner;
        if (owner != null)
            owner.setVisible(false);
        dialog.setSize(600,600);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

//    public static void main(String[] args) {
//        VideoApp dialog = new VideoApp();
//        dialog.setSize(400,400);
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);
//    }
}
