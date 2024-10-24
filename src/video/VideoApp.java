package video;

import dialogs.LineInput;
import common.Tools;
import database.DBHandler;

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
import java.util.List;

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
    public String snapDir = "C:\\Users\\Administrator\\Desktop\\snaps";
    private PlayerBox playerBox;
    private List<DBHandler.NameID> videoList;
    private List<DBHandler.NameID> gifList;
    private List<DBHandler.NameID> webpList;


    public VideoApp () {
        outputDirLabel.setText (snapDir);
        outputDirLabel.setToolTipText("Output Dir, klick to change ...");
        outputDirLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String dir = Tools.chooseDir(VideoApp.this)+File.separator;
                if (dir != null) {
                    snapDir = dir;
                    outputDirLabel.setText(snapDir);
                    repaint();
                }
            }
        });
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonPlay);

        buttonPlay.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);                                    
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (owner != null)
                    owner.setVisible(true);
                onCancel();
            }
        });

        setJListContent(false);

        enableDrop();

        deleteButton.addActionListener(e -> {
            DBHandler.NameID nameid = listControl.getSelectedValue();
            String name = listControl.getSelectedValue().name();
            if (gifList.contains(nameid)) {
                DBHandler.getInst().deleteGif(nameid.rowid());
            } else if (webpList.contains(nameid)) {
                DBHandler.getInst().deleteWEBP(nameid.rowid());
            } else {
                DBHandler.getInst().deleteVideo(nameid.rowid());
            }
            setJListContent(false);
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
            setJListContent(false);
        });

        listControl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    setJListContent(true);
                    repaint();
                }
            }
        });

        listControl.setCellRenderer(new MyCellRenderer());
    }

    private void onOK() {
        DBHandler.NameID nid = listControl.getSelectedValue();
        if (gifList.contains(nid)) {
            try {
                File f = DBHandler.getInst().transferGifIntoFile(nid);
                playerBox = new AnimPlayerBox(f, this, new GifDecoder());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (webpList.contains(nid)) {
            try {
                File f = DBHandler.getInst().transferwEBPIntoFile(nid);
                playerBox = new AnimPlayerBox(f, this, new WebPDecoder());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else {
            playerBox = new VideoPlayerBox(this, nid);
        }
        playerBox.start();
    }

    private void onCancel() {
        if (playerBox == null)
            return;
        playerBox.stop();
    }

    static class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {
        public Component getListCellRendererComponent(
                JList<?> list,           // the list
                Object value,            // value to display
                int index,               // cell index
                boolean isSelected,      // is the cell selected
                boolean cellHasFocus)    // does the cell have focus
        {
            String s = value.toString();
            if (s.contains(".gif :")) {
                setForeground(Color.RED);
            } else if (s.contains(".webp :")) {
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

    private void setJListContent(boolean sort_by_id) {
        videoList = DBHandler.getInst().getVideoFileNames(sort_by_id);
        gifList = DBHandler.getInst().getGifFileNames(sort_by_id);
        webpList = DBHandler.getInst().getWebPFileNames(sort_by_id);
        DefaultListModel<DBHandler.NameID> model = new DefaultListModel<>();
        model.addAll (videoList);
        model.addAll (gifList);
        model.addAll (webpList);
        listControl.setModel(model);
    }

    /**
     * DragDrop, only one vid
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
                        java.util.List<File> files;
                        try {
                            files = (java.util.List<File>) transferable.getTransferData(flavor);
                            File[] array = files.toArray(new File[0]);
                            File f = array[0];
                            if (Tools.isGIF(f.getPath())) {
                                DBHandler.getInst().addGifFile(f);
                            } else if (Tools.isWEBP(f.getPath())) {
                                DBHandler.getInst().addWebPFile(f);
                            } else {
                                DBHandler.getInst().addVideoFile(f);
                            }
                            f.delete();
                            setJListContent(false);
                            repaint();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return; // only one file
                    }
                }
            }
        });
    }

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
