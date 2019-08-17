package java.sql;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public interface Connection extends Wrapper, AutoCloseable {
  public static final int TRANSACTION_NONE = 0;
  
  public static final int TRANSACTION_READ_UNCOMMITTED = 1;
  
  public static final int TRANSACTION_READ_COMMITTED = 2;
  
  public static final int TRANSACTION_REPEATABLE_READ = 4;
  
  public static final int TRANSACTION_SERIALIZABLE = 8;
  
  Statement createStatement() throws SQLException;
  
  PreparedStatement prepareStatement(String paramString) throws SQLException;
  
  CallableStatement prepareCall(String paramString) throws SQLException;
  
  String nativeSQL(String paramString) throws SQLException;
  
  void setAutoCommit(boolean paramBoolean) throws SQLException;
  
  boolean getAutoCommit() throws SQLException;
  
  void commit() throws SQLException;
  
  void rollback() throws SQLException;
  
  void close() throws SQLException;
  
  boolean isClosed() throws SQLException;
  
  DatabaseMetaData getMetaData() throws SQLException;
  
  void setReadOnly(boolean paramBoolean) throws SQLException;
  
  boolean isReadOnly() throws SQLException;
  
  void setCatalog(String paramString) throws SQLException;
  
  String getCatalog() throws SQLException;
  
  void setTransactionIsolation(int paramInt) throws SQLException;
  
  int getTransactionIsolation() throws SQLException;
  
  SQLWarning getWarnings() throws SQLException;
  
  void clearWarnings() throws SQLException;
  
  Statement createStatement(int paramInt1, int paramInt2) throws SQLException;
  
  PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2) throws SQLException;
  
  CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2) throws SQLException;
  
  Map<String, Class<?>> getTypeMap() throws SQLException;
  
  void setTypeMap(Map<String, Class<?>> paramMap) throws SQLException;
  
  void setHoldability(int paramInt) throws SQLException;
  
  int getHoldability() throws SQLException;
  
  Savepoint setSavepoint() throws SQLException;
  
  Savepoint setSavepoint(String paramString) throws SQLException;
  
  void rollback(Savepoint paramSavepoint) throws SQLException;
  
  void releaseSavepoint(Savepoint paramSavepoint) throws SQLException;
  
  Statement createStatement(int paramInt1, int paramInt2, int paramInt3) throws SQLException;
  
  PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3) throws SQLException;
  
  CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2, int paramInt3) throws SQLException;
  
  PreparedStatement prepareStatement(String paramString, int paramInt) throws SQLException;
  
  PreparedStatement prepareStatement(String paramString, int[] paramArrayOfInt) throws SQLException;
  
  PreparedStatement prepareStatement(String paramString, String[] paramArrayOfString) throws SQLException;
  
  Clob createClob() throws SQLException;
  
  Blob createBlob() throws SQLException;
  
  NClob createNClob() throws SQLException;
  
  SQLXML createSQLXML() throws SQLException;
  
  boolean isValid(int paramInt) throws SQLException;
  
  void setClientInfo(String paramString1, String paramString2) throws SQLClientInfoException;
  
  void setClientInfo(Properties paramProperties) throws SQLClientInfoException;
  
  String getClientInfo(String paramString) throws SQLException;
  
  Properties getClientInfo() throws SQLException;
  
  Array createArrayOf(String paramString, Object[] paramArrayOfObject) throws SQLException;
  
  Struct createStruct(String paramString, Object[] paramArrayOfObject) throws SQLException;
  
  void setSchema(String paramString) throws SQLException;
  
  String getSchema() throws SQLException;
  
  void abort(Executor paramExecutor) throws SQLException;
  
  void setNetworkTimeout(Executor paramExecutor, int paramInt) throws SQLException;
  
  int getNetworkTimeout() throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */