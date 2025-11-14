package common;

import java.awt.*;
import java.awt.datatransfer.*;

import static common.NumToText.convertLessThanOneThousand;

public class SystemClipboard {
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static final Transferable emptyTrans = new Transferable() {
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[0];
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) {
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

    /**
     * add IMG tags to every line
     */
    public static void completeImagelinks(String tag) {
        StringBuilder sb1 = new StringBuilder();
        String[] split = getArray();
        int count = 0;
        for (String s : split) {
            if (s.length() < 10)
                continue;
            if (!s.startsWith("http:"))
                continue;
            sb1.append("[").
                    append(tag).append("]").
                    append(s).
                    append("[/").
                    append (tag).append("]").
                    append("\r\n");
            count++;
        }
        System.out.println(sb1);
        drainClipboard();
        Sam.speak(convertLessThanOneThousand(count)+" image tags posted to clipboard");
        setString(sb1.toString());
    }
}
