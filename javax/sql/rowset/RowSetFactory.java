package javax.sql.rowset;

import java.sql.SQLException;

public interface RowSetFactory {
  CachedRowSet createCachedRowSet() throws SQLException;
  
  FilteredRowSet createFilteredRowSet() throws SQLException;
  
  JdbcRowSet createJdbcRowSet() throws SQLException;
  
  JoinRowSet createJoinRowSet() throws SQLException;
  
  WebRowSet createWebRowSet() throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\RowSetFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */