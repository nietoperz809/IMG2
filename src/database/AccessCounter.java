package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public class AccessCounter extends DBHandler {
    private static final HashMap<Integer, Integer> cache = new HashMap<>();

    public static synchronized void incAccCounter(int rowid) {
        Integer val = cache.get(rowid);
        if (val != null)
            cache.put(rowid, val+1);
        String sql = "update IMAGES set ACCNUM = (ACCNUM + 1) where _rowid_ =" + rowid;
        try {
            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void setAccCounter(int rowid, int val) {
        cache.put(rowid, val);
        String sql = "update IMAGES set ACCNUM = " + val + " where _rowid_ =" + rowid;
        try {
            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized int getAccCounter(int rowid) {
        Integer ret = cache.get(rowid);
        if (ret != null)
            return ret;
        String q = "select ACCNUM from IMAGES where _rowid_ =" + rowid;
        try (ResultSet res = query(q)) {
            if (Objects.requireNonNull(res).next()) {
                System.out.println("readACC: " + rowid);
                ret = res.getInt(1);
                // init with 1 on first use
                if (ret == 0) {
                    setAccCounter(rowid, 1);
                    return 1;
                }
                cache.put(rowid, ret);
                return ret;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
}
