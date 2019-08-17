package java.sql;

public interface Statement extends Wrapper, AutoCloseable {
  public static final int CLOSE_CURRENT_RESULT = 1;
  
  public static final int KEEP_CURRENT_RESULT = 2;
  
  public static final int CLOSE_ALL_RESULTS = 3;
  
  public static final int SUCCESS_NO_INFO = -2;
  
  public static final int EXECUTE_FAILED = -3;
  
  public static final int RETURN_GENERATED_KEYS = 1;
  
  public static final int NO_GENERATED_KEYS = 2;
  
  ResultSet executeQuery(String paramString) throws SQLException;
  
  int executeUpdate(String paramString) throws SQLException;
  
  void close() throws SQLException;
  
  int getMaxFieldSize() throws SQLException;
  
  void setMaxFieldSize(int paramInt) throws SQLException;
  
  int getMaxRows() throws SQLException;
  
  void setMaxRows(int paramInt) throws SQLException;
  
  void setEscapeProcessing(boolean paramBoolean) throws SQLException;
  
  int getQueryTimeout() throws SQLException;
  
  void setQueryTimeout(int paramInt) throws SQLException;
  
  void cancel() throws SQLException;
  
  SQLWarning getWarnings() throws SQLException;
  
  void clearWarnings() throws SQLException;
  
  void setCursorName(String paramString) throws SQLException;
  
  boolean execute(String paramString) throws SQLException;
  
  ResultSet getResultSet() throws SQLException;
  
  int getUpdateCount() throws SQLException;
  
  boolean getMoreResults() throws SQLException;
  
  void setFetchDirection(int paramInt) throws SQLException;
  
  int getFetchDirection() throws SQLException;
  
  void setFetchSize(int paramInt) throws SQLException;
  
  int getFetchSize() throws SQLException;
  
  int getResultSetConcurrency() throws SQLException;
  
  int getResultSetType() throws SQLException;
  
  void addBatch(String paramString) throws SQLException;
  
  void clearBatch() throws SQLException;
  
  int[] executeBatch() throws SQLException;
  
  Connection getConnection() throws SQLException;
  
  boolean getMoreResults(int paramInt) throws SQLException;
  
  ResultSet getGeneratedKeys() throws SQLException;
  
  int executeUpdate(String paramString, int paramInt) throws SQLException;
  
  int executeUpdate(String paramString, int[] paramArrayOfInt) throws SQLException;
  
  int executeUpdate(String paramString, String[] paramArrayOfString) throws SQLException;
  
  boolean execute(String paramString, int paramInt) throws SQLException;
  
  boolean execute(String paramString, int[] paramArrayOfInt) throws SQLException;
  
  boolean execute(String paramString, String[] paramArrayOfString) throws SQLException;
  
  int getResultSetHoldability() throws SQLException;
  
  boolean isClosed() throws SQLException;
  
  void setPoolable(boolean paramBoolean) throws SQLException;
  
  boolean isPoolable() throws SQLException;
  
  void closeOnCompletion() throws SQLException;
  
  boolean isCloseOnCompletion() throws SQLException;
  
  default long getLargeUpdateCount() throws SQLException { throw new UnsupportedOperationException("getLargeUpdateCount not implemented"); }
  
  default void setLargeMaxRows(long paramLong) throws SQLException { throw new UnsupportedOperationException("setLargeMaxRows not implemented"); }
  
  default long getLargeMaxRows() throws SQLException { return 0L; }
  
  default long[] executeLargeBatch() throws SQLException { throw new UnsupportedOperationException("executeLargeBatch not implemented"); }
  
  default long executeLargeUpdate(String paramString) throws SQLException { throw new UnsupportedOperationException("executeLargeUpdate not implemented"); }
  
  default long executeLargeUpdate(String paramString, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented"); }
  
  default long executeLargeUpdate(String paramString, int[] paramArrayOfInt) throws SQLException { throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented"); }
  
  default long executeLargeUpdate(String paramString, String[] paramArrayOfString) throws SQLException { throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Statement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */