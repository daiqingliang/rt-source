package com.sun.rowset;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.WebRowSet;

public final class RowSetFactoryImpl implements RowSetFactory {
  public CachedRowSet createCachedRowSet() throws SQLException { return new CachedRowSetImpl(); }
  
  public FilteredRowSet createFilteredRowSet() throws SQLException { return new FilteredRowSetImpl(); }
  
  public JdbcRowSet createJdbcRowSet() throws SQLException { return new JdbcRowSetImpl(); }
  
  public JoinRowSet createJoinRowSet() throws SQLException { return new JoinRowSetImpl(); }
  
  public WebRowSet createWebRowSet() throws SQLException { return new WebRowSetImpl(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\RowSetFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */