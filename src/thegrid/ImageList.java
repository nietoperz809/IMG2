package thegrid;

import database.DBHandler;

import static java.util.Objects.requireNonNull;

public class ImageList {

    private String sql;

    public ImageList() {
    }

    // select name,_ROWID_,tag,accnum from IMAGES where tag = 'samen'

    public java.util.List<DBHandler.NameID> allFiles;

    public void refresh() {
        allFiles = requireNonNull(DBHandler.getInst())
                .loadImageInfosTopDown(this.sql);
        //recoverThumbs();
    }

    public DBHandler.NameID get (int n) {
        return allFiles.get(n);
    }

    public int size() {
        return allFiles.size();
    }

    public void add (DBHandler.NameID nid) {
        allFiles.add(nid);
    }

    public int getLastRowid() {
        refresh();
        return allFiles.get (allFiles.size()-1).rowid();
    }

    public int IndexByRowID(int rowid) {
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

    public void setSQL(String sql) {
        this.sql = sql;
        refresh();
    }

    public String getSql() {
        return this.sql;
    }

    private boolean rec = false;

    public void recoverThumbs() {
        if (rec) return;
        rec = true;
        System.out.println("StartThumbRecovery!");
        for (DBHandler.NameID elem : allFiles) {
            System.out.println(elem.rowid());
            DBHandler.getInst().createNewThumb(elem.rowid());
        }
    }
}
