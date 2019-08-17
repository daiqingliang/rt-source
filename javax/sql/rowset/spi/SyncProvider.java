package javax.sql.rowset.spi;

import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;

public abstract class SyncProvider {
  public static final int GRADE_NONE = 1;
  
  public static final int GRADE_CHECK_MODIFIED_AT_COMMIT = 2;
  
  public static final int GRADE_CHECK_ALL_AT_COMMIT = 3;
  
  public static final int GRADE_LOCK_WHEN_MODIFIED = 4;
  
  public static final int GRADE_LOCK_WHEN_LOADED = 5;
  
  public static final int DATASOURCE_NO_LOCK = 1;
  
  public static final int DATASOURCE_ROW_LOCK = 2;
  
  public static final int DATASOURCE_TABLE_LOCK = 3;
  
  public static final int DATASOURCE_DB_LOCK = 4;
  
  public static final int UPDATABLE_VIEW_SYNC = 5;
  
  public static final int NONUPDATABLE_VIEW_SYNC = 6;
  
  public abstract String getProviderID();
  
  public abstract RowSetReader getRowSetReader();
  
  public abstract RowSetWriter getRowSetWriter();
  
  public abstract int getProviderGrade();
  
  public abstract void setDataSourceLock(int paramInt) throws SyncProviderException;
  
  public abstract int getDataSourceLock();
  
  public abstract int supportsUpdatableView();
  
  public abstract String getVersion();
  
  public abstract String getVendor();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\spi\SyncProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */