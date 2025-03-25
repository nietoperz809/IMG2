package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class AccessCounter extends DBHandler {
    public static synchronized void incAccCounter(int rowid) {
        String sql = "update IMAGES set ACCNUM = (ACCNUM + 1) where _rowid_ =" + rowid;
        try {
            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void setAccCounter(int rowid, int val) {
        String sql = "update IMAGES set ACCNUM = " + val + " where _rowid_ =" + rowid;
        try {
            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized int getAccCounter(int rowid) {
        String q = "select ACCNUM from IMAGES where _rowid_ =" + rowid;
        try (ResultSet res = query(q)) {
            if (Objects.requireNonNull(res).next()) {
                System.out.println("readACC: " + rowid);
                int ret = res.getInt(1);
                // init with 1 on first use
                if (ret == 0) {
                    setAccCounter(rowid, 1);
                    return 1;
                }
                return ret;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
}
