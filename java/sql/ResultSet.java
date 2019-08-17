package java.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

public interface ResultSet extends Wrapper, AutoCloseable {
  public static final int FETCH_FORWARD = 1000;
  
  public static final int FETCH_REVERSE = 1001;
  
  public static final int FETCH_UNKNOWN = 1002;
  
  public static final int TYPE_FORWARD_ONLY = 1003;
  
  public static final int TYPE_SCROLL_INSENSITIVE = 1004;
  
  public static final int TYPE_SCROLL_SENSITIVE = 1005;
  
  public static final int CONCUR_READ_ONLY = 1007;
  
  public static final int CONCUR_UPDATABLE = 1008;
  
  public static final int HOLD_CURSORS_OVER_COMMIT = 1;
  
  public static final int CLOSE_CURSORS_AT_COMMIT = 2;
  
  boolean next() throws SQLException;
  
  void close() throws SQLException;
  
  boolean wasNull() throws SQLException;
  
  String getString(int paramInt) throws SQLException;
  
  boolean getBoolean(int paramInt) throws SQLException;
  
  byte getByte(int paramInt) throws SQLException;
  
  short getShort(int paramInt) throws SQLException;
  
  int getInt(int paramInt) throws SQLException;
  
  long getLong(int paramInt) throws SQLException;
  
  float getFloat(int paramInt) throws SQLException;
  
  double getDouble(int paramInt) throws SQLException;
  
  @Deprecated
  BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException;
  
  byte[] getBytes(int paramInt) throws SQLException;
  
  Date getDate(int paramInt) throws SQLException;
  
  Time getTime(int paramInt) throws SQLException;
  
  Timestamp getTimestamp(int paramInt) throws SQLException;
  
  InputStream getAsciiStream(int paramInt) throws SQLException;
  
  @Deprecated
  InputStream getUnicodeStream(int paramInt) throws SQLException;
  
  InputStream getBinaryStream(int paramInt) throws SQLException;
  
  String getString(String paramString) throws SQLException;
  
  boolean getBoolean(String paramString) throws SQLException;
  
  byte getByte(String paramString) throws SQLException;
  
  short getShort(String paramString) throws SQLException;
  
  int getInt(String paramString) throws SQLException;
  
  long getLong(String paramString) throws SQLException;
  
  float getFloat(String paramString) throws SQLException;
  
  double getDouble(String paramString) throws SQLException;
  
  @Deprecated
  BigDecimal getBigDecimal(String paramString, int paramInt) throws SQLException;
  
  byte[] getBytes(String paramString) throws SQLException;
  
  Date getDate(String paramString) throws SQLException;
  
  Time getTime(String paramString) throws SQLException;
  
  Timestamp getTimestamp(String paramString) throws SQLException;
  
  InputStream getAsciiStream(String paramString) throws SQLException;
  
  @Deprecated
  InputStream getUnicodeStream(String paramString) throws SQLException;
  
  InputStream getBinaryStream(String paramString) throws SQLException;
  
  SQLWarning getWarnings() throws SQLException;
  
  void clearWarnings() throws SQLException;
  
  String getCursorName() throws SQLException;
  
  ResultSetMetaData getMetaData() throws SQLException;
  
  Object getObject(int paramInt) throws SQLException;
  
  Object getObject(String paramString) throws SQLException;
  
  int findColumn(String paramString) throws SQLException;
  
  Reader getCharacterStream(int paramInt) throws SQLException;
  
  Reader getCharacterStream(String paramString) throws SQLException;
  
  BigDecimal getBigDecimal(int paramInt) throws SQLException;
  
  BigDecimal getBigDecimal(String paramString) throws SQLException;
  
  boolean isBeforeFirst() throws SQLException;
  
  boolean isAfterLast() throws SQLException;
  
  boolean isFirst() throws SQLException;
  
  boolean isLast() throws SQLException;
  
  void beforeFirst() throws SQLException;
  
  void afterLast() throws SQLException;
  
  boolean first() throws SQLException;
  
  boolean last() throws SQLException;
  
  int getRow() throws SQLException;
  
  boolean absolute(int paramInt) throws SQLException;
  
  boolean relative(int paramInt) throws SQLException;
  
  boolean previous() throws SQLException;
  
  void setFetchDirection(int paramInt) throws SQLException;
  
  int getFetchDirection() throws SQLException;
  
  void setFetchSize(int paramInt) throws SQLException;
  
  int getFetchSize() throws SQLException;
  
  int getType() throws SQLException;
  
  int getConcurrency() throws SQLException;
  
  boolean rowUpdated() throws SQLException;
  
  boolean rowInserted() throws SQLException;
  
  boolean rowDeleted() throws SQLException;
  
  void updateNull(int paramInt) throws SQLException;
  
  void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException;
  
  void updateByte(int paramInt, byte paramByte) throws SQLException;
  
  void updateShort(int paramInt, short paramShort) throws SQLException;
  
  void updateInt(int paramInt1, int paramInt2) throws SQLException;
  
  void updateLong(int paramInt, long paramLong) throws SQLException;
  
  void updateFloat(int paramInt, float paramFloat) throws SQLException;
  
  void updateDouble(int paramInt, double paramDouble) throws SQLException;
  
  void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException;
  
  void updateString(int paramInt, String paramString) throws SQLException;
  
  void updateBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException;
  
  void updateDate(int paramInt, Date paramDate) throws SQLException;
  
  void updateTime(int paramInt, Time paramTime) throws SQLException;
  
  void updateTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException;
  
  void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException;
  
  void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException;
  
  void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException;
  
  void updateObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException;
  
  void updateObject(int paramInt, Object paramObject) throws SQLException;
  
  void updateNull(String paramString) throws SQLException;
  
  void updateBoolean(String paramString, boolean paramBoolean) throws SQLException;
  
  void updateByte(String paramString, byte paramByte) throws SQLException;
  
  void updateShort(String paramString, short paramShort) throws SQLException;
  
  void updateInt(String paramString, int paramInt) throws SQLException;
  
  void updateLong(String paramString, long paramLong) throws SQLException;
  
  void updateFloat(String paramString, float paramFloat) throws SQLException;
  
  void updateDouble(String paramString, double paramDouble) throws SQLException;
  
  void updateBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException;
  
  void updateString(String paramString1, String paramString2) throws SQLException;
  
  void updateBytes(String paramString, byte[] paramArrayOfByte) throws SQLException;
  
  void updateDate(String paramString, Date paramDate) throws SQLException;
  
  void updateTime(String paramString, Time paramTime) throws SQLException;
  
  void updateTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException;
  
  void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException;
  
  void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException;
  
  void updateCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException;
  
  void updateObject(String paramString, Object paramObject, int paramInt) throws SQLException;
  
  void updateObject(String paramString, Object paramObject) throws SQLException;
  
  void insertRow() throws SQLException;
  
  void updateRow() throws SQLException;
  
  void deleteRow() throws SQLException;
  
  void refreshRow() throws SQLException;
  
  void cancelRowUpdates() throws SQLException;
  
  void moveToInsertRow() throws SQLException;
  
  void moveToCurrentRow() throws SQLException;
  
  Statement getStatement() throws SQLException;
  
  Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException;
  
  Ref getRef(int paramInt) throws SQLException;
  
  Blob getBlob(int paramInt) throws SQLException;
  
  Clob getClob(int paramInt) throws SQLException;
  
  Array getArray(int paramInt) throws SQLException;
  
  Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException;
  
  Ref getRef(String paramString) throws SQLException;
  
  Blob getBlob(String paramString) throws SQLException;
  
  Clob getClob(String paramString) throws SQLException;
  
  Array getArray(String paramString) throws SQLException;
  
  Date getDate(int paramInt, Calendar paramCalendar) throws SQLException;
  
  Date getDate(String paramString, Calendar paramCalendar) throws SQLException;
  
  Time getTime(int paramInt, Calendar paramCalendar) throws SQLException;
  
  Time getTime(String paramString, Calendar paramCalendar) throws SQLException;
  
  Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException;
  
  Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException;
  
  URL getURL(int paramInt) throws SQLException;
  
  URL getURL(String paramString) throws SQLException;
  
  void updateRef(int paramInt, Ref paramRef) throws SQLException;
  
  void updateRef(String paramString, Ref paramRef) throws SQLException;
  
  void updateBlob(int paramInt, Blob paramBlob) throws SQLException;
  
  void updateBlob(String paramString, Blob paramBlob) throws SQLException;
  
  void updateClob(int paramInt, Clob paramClob) throws SQLException;
  
  void updateClob(String paramString, Clob paramClob) throws SQLException;
  
  void updateArray(int paramInt, Array paramArray) throws SQLException;
  
  void updateArray(String paramString, Array paramArray) throws SQLException;
  
  RowId getRowId(int paramInt) throws SQLException;
  
  RowId getRowId(String paramString) throws SQLException;
  
  void updateRowId(int paramInt, RowId paramRowId) throws SQLException;
  
  void updateRowId(String paramString, RowId paramRowId) throws SQLException;
  
  int getHoldability() throws SQLException;
  
  boolean isClosed() throws SQLException;
  
  void updateNString(int paramInt, String paramString) throws SQLException;
  
  void updateNString(String paramString1, String paramString2) throws SQLException;
  
  void updateNClob(int paramInt, NClob paramNClob) throws SQLException;
  
  void updateNClob(String paramString, NClob paramNClob) throws SQLException;
  
  NClob getNClob(int paramInt) throws SQLException;
  
  NClob getNClob(String paramString) throws SQLException;
  
  SQLXML getSQLXML(int paramInt) throws SQLException;
  
  SQLXML getSQLXML(String paramString) throws SQLException;
  
  void updateSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException;
  
  void updateSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException;
  
  String getNString(int paramInt) throws SQLException;
  
  String getNString(String paramString) throws SQLException;
  
  Reader getNCharacterStream(int paramInt) throws SQLException;
  
  Reader getNCharacterStream(String paramString) throws SQLException;
  
  void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void updateNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void updateCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void updateCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void updateBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void updateBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void updateClob(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void updateClob(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void updateNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void updateNClob(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void updateNCharacterStream(int paramInt, Reader paramReader) throws SQLException;
  
  void updateNCharacterStream(String paramString, Reader paramReader) throws SQLException;
  
  void updateAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException;
  
  void updateBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException;
  
  void updateCharacterStream(int paramInt, Reader paramReader) throws SQLException;
  
  void updateAsciiStream(String paramString, InputStream paramInputStream) throws SQLException;
  
  void updateBinaryStream(String paramString, InputStream paramInputStream) throws SQLException;
  
  void updateCharacterStream(String paramString, Reader paramReader) throws SQLException;
  
  void updateBlob(int paramInt, InputStream paramInputStream) throws SQLException;
  
  void updateBlob(String paramString, InputStream paramInputStream) throws SQLException;
  
  void updateClob(int paramInt, Reader paramReader) throws SQLException;
  
  void updateClob(String paramString, Reader paramReader) throws SQLException;
  
  void updateNClob(int paramInt, Reader paramReader) throws SQLException;
  
  void updateNClob(String paramString, Reader paramReader) throws SQLException;
  
  <T> T getObject(int paramInt, Class<T> paramClass) throws SQLException;
  
  <T> T getObject(String paramString, Class<T> paramClass) throws SQLException;
  
  default void updateObject(int paramInt1, Object paramObject, SQLType paramSQLType, int paramInt2) throws SQLException { throw new SQLFeatureNotSupportedException("updateObject not implemented"); }
  
  default void updateObject(String paramString, Object paramObject, SQLType paramSQLType, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException("updateObject not implemented"); }
  
  default void updateObject(int paramInt, Object paramObject, SQLType paramSQLType) throws SQLException { throw new SQLFeatureNotSupportedException("updateObject not implemented"); }
  
  default void updateObject(String paramString, Object paramObject, SQLType paramSQLType) throws SQLException { throw new SQLFeatureNotSupportedException("updateObject not implemented"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\ResultSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */