package javax.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Wrapper;

public interface DataSource extends CommonDataSource, Wrapper {
  Connection getConnection() throws SQLException;
  
  Connection getConnection(String paramString1, String paramString2) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\DataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */