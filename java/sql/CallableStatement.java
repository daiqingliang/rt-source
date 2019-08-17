package java.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

public interface CallableStatement extends PreparedStatement {
  void registerOutParameter(int paramInt1, int paramInt2) throws SQLException;
  
  void registerOutParameter(int paramInt1, int paramInt2, int paramInt3) throws SQLException;
  
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
  
  Object getObject(int paramInt) throws SQLException;
  
  BigDecimal getBigDecimal(int paramInt) throws SQLException;
  
  Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException;
  
  Ref getRef(int paramInt) throws SQLException;
  
  Blob getBlob(int paramInt) throws SQLException;
  
  Clob getClob(int paramInt) throws SQLException;
  
  Array getArray(int paramInt) throws SQLException;
  
  Date getDate(int paramInt, Calendar paramCalendar) throws SQLException;
  
  Time getTime(int paramInt, Calendar paramCalendar) throws SQLException;
  
  Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException;
  
  void registerOutParameter(int paramInt1, int paramInt2, String paramString) throws SQLException;
  
  void registerOutParameter(String paramString, int paramInt) throws SQLException;
  
  void registerOutParameter(String paramString, int paramInt1, int paramInt2) throws SQLException;
  
  void registerOutParameter(String paramString1, int paramInt, String paramString2) throws SQLException;
  
  URL getURL(int paramInt) throws SQLException;
  
  void setURL(String paramString, URL paramURL) throws SQLException;
  
  void setNull(String paramString, int paramInt) throws SQLException;
  
  void setBoolean(String paramString, boolean paramBoolean) throws SQLException;
  
  void setByte(String paramString, byte paramByte) throws SQLException;
  
  void setShort(String paramString, short paramShort) throws SQLException;
  
  void setInt(String paramString, int paramInt) throws SQLException;
  
  void setLong(String paramString, long paramLong) throws SQLException;
  
  void setFloat(String paramString, float paramFloat) throws SQLException;
  
  void setDouble(String paramString, double paramDouble) throws SQLException;
  
  void setBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException;
  
  void setString(String paramString1, String paramString2) throws SQLException;
  
  void setBytes(String paramString, byte[] paramArrayOfByte) throws SQLException;
  
  void setDate(String paramString, Date paramDate) throws SQLException;
  
  void setTime(String paramString, Time paramTime) throws SQLException;
  
  void setTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException;
  
  void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException;
  
  void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException;
  
  void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2) throws SQLException;
  
  void setObject(String paramString, Object paramObject, int paramInt) throws SQLException;
  
  void setObject(String paramString, Object paramObject) throws SQLException;
  
  void setCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException;
  
  void setDate(String paramString, Date paramDate, Calendar paramCalendar) throws SQLException;
  
  void setTime(String paramString, Time paramTime, Calendar paramCalendar) throws SQLException;
  
  void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar) throws SQLException;
  
  void setNull(String paramString1, int paramInt, String paramString2) throws SQLException;
  
  String getString(String paramString) throws SQLException;
  
  boolean getBoolean(String paramString) throws SQLException;
  
  byte getByte(String paramString) throws SQLException;
  
  short getShort(String paramString) throws SQLException;
  
  int getInt(String paramString) throws SQLException;
  
  long getLong(String paramString) throws SQLException;
  
  float getFloat(String paramString) throws SQLException;
  
  double getDouble(String paramString) throws SQLException;
  
  byte[] getBytes(String paramString) throws SQLException;
  
  Date getDate(String paramString) throws SQLException;
  
  Time getTime(String paramString) throws SQLException;
  
  Timestamp getTimestamp(String paramString) throws SQLException;
  
  Object getObject(String paramString) throws SQLException;
  
  BigDecimal getBigDecimal(String paramString) throws SQLException;
  
  Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException;
  
  Ref getRef(String paramString) throws SQLException;
  
  Blob getBlob(String paramString) throws SQLException;
  
  Clob getClob(String paramString) throws SQLException;
  
  Array getArray(String paramString) throws SQLException;
  
  Date getDate(String paramString, Calendar paramCalendar) throws SQLException;
  
  Time getTime(String paramString, Calendar paramCalendar) throws SQLException;
  
  Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException;
  
  URL getURL(String paramString) throws SQLException;
  
  RowId getRowId(int paramInt) throws SQLException;
  
  RowId getRowId(String paramString) throws SQLException;
  
  void setRowId(String paramString, RowId paramRowId) throws SQLException;
  
  void setNString(String paramString1, String paramString2) throws SQLException;
  
  void setNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void setNClob(String paramString, NClob paramNClob) throws SQLException;
  
  void setClob(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void setBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void setNClob(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  NClob getNClob(int paramInt) throws SQLException;
  
  NClob getNClob(String paramString) throws SQLException;
  
  void setSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException;
  
  SQLXML getSQLXML(int paramInt) throws SQLException;
  
  SQLXML getSQLXML(String paramString) throws SQLException;
  
  String getNString(int paramInt) throws SQLException;
  
  String getNString(String paramString) throws SQLException;
  
  Reader getNCharacterStream(int paramInt) throws SQLException;
  
  Reader getNCharacterStream(String paramString) throws SQLException;
  
  Reader getCharacterStream(int paramInt) throws SQLException;
  
  Reader getCharacterStream(String paramString) throws SQLException;
  
  void setBlob(String paramString, Blob paramBlob) throws SQLException;
  
  void setClob(String paramString, Clob paramClob) throws SQLException;
  
  void setAsciiStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void setBinaryStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void setCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void setAsciiStream(String paramString, InputStream paramInputStream) throws SQLException;
  
  void setBinaryStream(String paramString, InputStream paramInputStream) throws SQLException;
  
  void setCharacterStream(String paramString, Reader paramReader) throws SQLException;
  
  void setNCharacterStream(String paramString, Reader paramReader) throws SQLException;
  
  void setClob(String paramString, Reader paramReader) throws SQLException;
  
  void setBlob(String paramString, InputStream paramInputStream) throws SQLException;
  
  void setNClob(String paramString, Reader paramReader) throws SQLException;
  
  <T> T getObject(int paramInt, Class<T> paramClass) throws SQLException;
  
  <T> T getObject(String paramString, Class<T> paramClass) throws SQLException;
  
  default void setObject(String paramString, Object paramObject, SQLType paramSQLType, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException("setObject not implemented"); }
  
  default void setObject(String paramString, Object paramObject, SQLType paramSQLType) throws SQLException { throw new SQLFeatureNotSupportedException("setObject not implemented"); }
  
  default void registerOutParameter(int paramInt, SQLType paramSQLType) throws SQLException { throw new SQLFeatureNotSupportedException("registerOutParameter not implemented"); }
  
  default void registerOutParameter(int paramInt1, SQLType paramSQLType, int paramInt2) throws SQLException { throw new SQLFeatureNotSupportedException("registerOutParameter not implemented"); }
  
  default void registerOutParameter(int paramInt, SQLType paramSQLType, String paramString) throws SQLException { throw new SQLFeatureNotSupportedException("registerOutParameter not implemented"); }
  
  default void registerOutParameter(String paramString, SQLType paramSQLType) throws SQLException { throw new SQLFeatureNotSupportedException("registerOutParameter not implemented"); }
  
  default void registerOutParameter(String paramString, SQLType paramSQLType, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException("registerOutParameter not implemented"); }
  
  default void registerOutParameter(String paramString1, SQLType paramSQLType, String paramString2) throws SQLException { throw new SQLFeatureNotSupportedException("registerOutParameter not implemented"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\CallableStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */