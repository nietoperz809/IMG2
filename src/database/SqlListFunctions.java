package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlListFunctions extends DBHandler {

    public static ArrayList<GridQuery> getQueries() {
        ArrayList<GridQuery> al = new ArrayList<>();
        try {
            ResultSet res = query("select entry,_rowid_ from queries");
            if (res == null)
                throw new RuntimeException("no query results");
            while (res.next()) {
                byte[] bt = res.getBytes(1);
                int rowid = res.getInt(2);
                al.add (new GridQuery(new String(bt),rowid));
            }
            res.close();
            return al;
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static void putQuery (String str) {
        PreparedStatement prep;
        try {
            prep = connection.prepareStatement(
                    "merge into QUERIES(entry) key(entry) values (?)");
            prep.setBytes(1, str.getBytes());
            prep.execute();
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static void deleteQuery (int rowid) {
        String sql = "delete from QUERIES where _rowid_ = " + rowid;
        execSQL(sql);
    }
}
