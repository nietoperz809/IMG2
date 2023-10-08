package thegrid;

import database.DBHandler;

import static java.util.Objects.requireNonNull;

public class ImageList {

    private ImageList() {
        // prevent instantiation
    }
    
    private static java.util.List<DBHandler.NameID> allFiles
            = requireNonNull(DBHandler.getInst()).getAllImageInfos();

    public static void refresh() {
        allFiles = requireNonNull(DBHandler.getInst()).getAllImageInfos();
    }

    public static DBHandler.NameID get (int n) {
        return allFiles.get(n);
    }

    public static int size() {
        return allFiles.size();
    }

    public static void add (DBHandler.NameID nid) {
        allFiles.add(nid);
    }

    public static int getLastRowid() {
        refresh();
        return allFiles.get (allFiles.size()-1).rowid();
    }

    public static int IndexByRowID(int rowid) {
        refresh();
        if (rowid == -1) {  // last rowid
            return size()-1;
        }
        for (int n=0; n<size(); n++) {
            if (get(n).rowid() == rowid) {
                return n;
            }
        }
        return -1;
    }

}
