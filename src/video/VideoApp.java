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
        //setModal(true);
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

        setListContent(false);

        enableDrop();
        deleteButton.addActionListener(e -> {
            int id = listControl.getSelectedValue().rowid();
            String name = listControl.getSelectedValue().name();
            if (Tools.isGIF(name)) {
                DBHandler.getInst().deleteGif(id);
            } else if (Tools.isWEBP(name)) {
                DBHandler.getInst().deleteWEBP(id);
            } else {
                DBHandler.getInst().deleteVideo(id);
            }
            setListContent(false);
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
                    if (Tools.isGIF(nameid.name())) {
                        bt = DBHandler.getInst().loadGifBytes(nameid);
                    } else if (Tools.isWEBP(nameid.name())) {
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
            if (Tools.isGIF(nameid.name())) {
                Tools.Error ("GIF renaming currently not allowed");
                return;
            }
            String res = LineInput.xmain(nameid.name(), "NewName", Color.orange);
            DBHandler.getInst().changeVideoName(res, nameid.rowid());
            setListContent(false);
        });
        listControl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    setListContent(true);
                    repaint();
                }
            }
        });
        listControl.setCellRenderer(new MyCellRenderer());
    }

    final VideoPlayerBox vidPlayerBox = new VideoPlayerBox(this);
    // GifPlayerBox gifPlayer = new GifPlayerBox();

    private void onOK() {
        DBHandler.NameID nid = listControl.getSelectedValue();
        listControl.getSelectedValue();
        if (Tools.isGIF(nid.name())) {
            try {
                File f = DBHandler.getInst().transferGifIntoFile(nid);
                new AnimPlayerBox(f, this, new GifDecoder());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (Tools.isWEBP(nid.name())) {
            try {
                File f = DBHandler.getInst().transferwEBPIntoFile(nid);
                new AnimPlayerBox(f, this, new WebPDecoder());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        else {
            vidPlayerBox.start (nid);
        }
    }

    private void onCancel() {
        vidPlayerBox.stop();
    }

    class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {
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
                setForeground(Color.GREEN);
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

    private void setListContent(boolean sort_by_id) {
        List<DBHandler.NameID> list = DBHandler.getInst().getVideoFileNames(sort_by_id);
        List<DBHandler.NameID> list2 = DBHandler.getInst().getGifFileNames(sort_by_id);
        List<DBHandler.NameID> list3 = DBHandler.getInst().getWebPFileNames(sort_by_id);
        DefaultListModel<DBHandler.NameID> model = new DefaultListModel<>();
        model.addAll (list);
        model.addAll (list2);
        model.addAll (list3);
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
                            setListContent(false);
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

    public static void main(String[] args) {
        VideoApp dialog = new VideoApp();
        dialog.setSize(400,400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
