package javax.sql.rowset.spi;

import java.sql.SQLException;
import javax.sql.RowSet;

public interface SyncResolver extends RowSet {
  public static final int UPDATE_ROW_CONFLICT = 0;
  
  public static final int DELETE_ROW_CONFLICT = 1;
  
  public static final int INSERT_ROW_CONFLICT = 2;
  
  public static final int NO_ROW_CONFLICT = 3;
  
  int getStatus();
  
  Object getConflictValue(int paramInt) throws SQLException;
  
  Object getConflictValue(String paramString) throws SQLException;
  
  void setResolvedValue(int paramInt, Object paramObject) throws SQLException;
  
  void setResolvedValue(String paramString, Object paramObject) throws SQLException;
  
  boolean nextConflict() throws SQLException;
  
  boolean previousConflict() throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\spi\SyncResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */