package thegrid;

import database.DBHandler;

public class ImageList {

    public static final String mainSQL = "select * from (select name,_ROWID_,tag,accnum from IMAGES) order by _rowid_ desc";
    private String sql;

    public ImageList() {
    }

    // select name,_ROWID_,tag,accnum from IMAGES where tag = 'samen'

    // select * from (select name,_ROWID_,tag,accnum from IMAGES) order by _rowid_ desc

    public java.util.List<DBHandler.NameID> allFiles;

    public void refresh() {
//        if (grid.thisInstCount == 1)
//            allFiles = DBHandler.loadImageInfosTopDown(this.sql);
//        else
            allFiles = DBHandler.loadImageInfos(this.sql);
    }

    public DBHandler.NameID get (int n) {
        return allFiles.get(n);
    }

    public int size() {
        return allFiles.size();
    }

    public void addNameID(DBHandler.NameID nid) {
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

    public void setSQL(String sql, TheGrid theGrid) {
        this.sql = sql;
        refresh();
    }

    public String getSql() {
        return this.sql;
    }
}
