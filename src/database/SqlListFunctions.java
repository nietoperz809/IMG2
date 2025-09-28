package database;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlListFunctions extends DBHandler {

    public static ArrayList<GridQuery> getQueries() {
        ArrayList<GridQuery> al = new ArrayList<>();
        try {
            ResultSet res = query("select sql,_rowid_ from queries");
            if (res == null)
                throw new RuntimeException("no query results");
            while (res.next()) {
                byte[] bt = res.getBytes(1);
                int rowid = res.getInt(2);
                al.add (new GridQuery(new String(bt, StandardCharsets.UTF_8),rowid));
            }
            res.close();
            return al;
        } catch (SQLException e) {
            //System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static void putQuery (String str) {
        String sql = "merge into QUERIES(sql) key(sql) values (?)";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setBytes(1, str.getBytes(StandardCharsets.UTF_8));
            prep.execute();
        } catch (SQLException e) {
            throw new RuntimeException (e);
        }
    }

    public static void deleteQuery (int rowid) {
        String sql = "delete from QUERIES where _rowid_ = " + rowid;
        execSQL(sql);
    }
}
