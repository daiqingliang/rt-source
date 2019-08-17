package javax.sql.rowset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Collection;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;

public interface CachedRowSet extends RowSet, Joinable {
  @Deprecated
  public static final boolean COMMIT_ON_ACCEPT_CHANGES = true;
  
  void populate(ResultSet paramResultSet) throws SQLException;
  
  void execute(Connection paramConnection) throws SQLException;
  
  void acceptChanges() throws SyncProviderException;
  
  void acceptChanges(Connection paramConnection) throws SQLException;
  
  void restoreOriginal() throws SyncProviderException;
  
  void release() throws SyncProviderException;
  
  void undoDelete() throws SyncProviderException;
  
  void undoInsert() throws SyncProviderException;
  
  void undoUpdate() throws SyncProviderException;
  
  boolean columnUpdated(int paramInt) throws SQLException;
  
  boolean columnUpdated(String paramString) throws SQLException;
  
  Collection<?> toCollection() throws SQLException;
  
  Collection<?> toCollection(int paramInt) throws SQLException;
  
  Collection<?> toCollection(String paramString) throws SQLException;
  
  SyncProvider getSyncProvider() throws SQLException;
  
  void setSyncProvider(String paramString) throws SQLException;
  
  int size();
  
  void setMetaData(RowSetMetaData paramRowSetMetaData) throws SQLException;
  
  ResultSet getOriginal() throws SQLException;
  
  ResultSet getOriginalRow() throws SQLException;
  
  void setOriginalRow() throws SyncProviderException;
  
  String getTableName() throws SQLException;
  
  void setTableName(String paramString) throws SQLException;
  
  int[] getKeyColumns() throws SQLException;
  
  void setKeyColumns(int[] paramArrayOfInt) throws SQLException;
  
  RowSet createShared() throws SQLException;
  
  CachedRowSet createCopy() throws SQLException;
  
  CachedRowSet createCopySchema() throws SQLException;
  
  CachedRowSet createCopyNoConstraints() throws SQLException;
  
  RowSetWarning getRowSetWarnings() throws SQLException;
  
  boolean getShowDeleted() throws SQLException;
  
  void setShowDeleted(boolean paramBoolean) throws SQLException;
  
  void commit() throws SyncProviderException;
  
  void rollback() throws SyncProviderException;
  
  void rollback(Savepoint paramSavepoint) throws SQLException;
  
  void rowSetPopulated(RowSetEvent paramRowSetEvent, int paramInt) throws SQLException;
  
  void populate(ResultSet paramResultSet, int paramInt) throws SQLException;
  
  void setPageSize(int paramInt) throws SQLException;
  
  int getPageSize();
  
  boolean nextPage() throws SQLException;
  
  boolean previousPage() throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\CachedRowSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */