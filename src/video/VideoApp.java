package video;

import common.LineInput;
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
    public String snapDir = "C:\\Users\\Administrator\\Desktop\\snaps\\";

    public VideoApp () {
        outputDirLabel.setText (snapDir);
        outputDirLabel.setToolTipText("Output Dir, klick to change ...");
        outputDirLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                snapDir = Tools.chooseDir(VideoApp.this)+File.separator;
                outputDirLabel.setText (snapDir);
                repaint();
            }
        });
        setContentPane(contentPane);
        //setModal(true);
        getRootPane().setDefaultButton(buttonPlay);

        buttonPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);                                    
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (owner != null)
                    owner.setEnabled(true);
                onCancel();
            }
        });

        setListContent();

        enableDrop();
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = listControl.getSelectedValue().rowid;
                DBHandler.getInst().deleteVideo(id);
                setListContent();
                repaint();
            }
        });
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DBHandler.NameID nameid = listControl.getSelectedValue();
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File(nameid.name));
                    int option = fileChooser.showSaveDialog(VideoApp.this);
                    if(option == JFileChooser.APPROVE_OPTION){
                        SoftReference<byte[]> bt = DBHandler.getInst().loadVideoBytes(nameid.name);
                        File f = fileChooser.getSelectedFile();
                        Files.write(f.toPath(),bt.get());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DBHandler.NameID nameid = listControl.getSelectedValue();
                String res = LineInput.xmain();
                DBHandler.getInst().changeVideoName(res, nameid.rowid);
                setListContent();
            }
        });
    }

    MediaPlayerBox playerBox = new MediaPlayerBox(this);

    private void onOK() {
        playerBox.start (listControl.getSelectedValue().name);
    }

    private void onCancel() {
        playerBox.stop();
    }

    private void setListContent() {
        List<DBHandler.NameID> list = DBHandler.getInst().getVideoFileNames();
        DefaultListModel<DBHandler.NameID> model = new DefaultListModel<>();
        model.addAll (list);
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
                            DBHandler.getInst().addVideoFile(array[0]);
                            setListContent();
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
            owner.setEnabled(false);
        dialog.setSize(400,400);
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
