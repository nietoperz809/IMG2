package common;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class SystemClipboard {
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static Transferable emptyTrans = new Transferable() {
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[0];
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return null;
        }
    };

    public static void drainClipboard() {
        clipboard.setContents(emptyTrans, null);
    }

    public static void setString(String str) {
        clipboard.setContents (new StringSelection(str), null);
    }

    public static String[] getArray() {
        try {
            String result = (String) clipboard.getData(DataFlavor.stringFlavor);
            return result.split("[\\r\\n]");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
