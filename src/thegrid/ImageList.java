package thegrid;

import database.DBHandler;

import static java.util.Objects.requireNonNull;

public class ImageList {

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
}
