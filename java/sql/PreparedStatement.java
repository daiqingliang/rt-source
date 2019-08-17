package java.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;

public interface PreparedStatement extends Statement {
  ResultSet executeQuery() throws SQLException;
  
  int executeUpdate() throws SQLException;
  
  void setNull(int paramInt1, int paramInt2) throws SQLException;
  
  void setBoolean(int paramInt, boolean paramBoolean) throws SQLException;
  
  void setByte(int paramInt, byte paramByte) throws SQLException;
  
  void setShort(int paramInt, short paramShort) throws SQLException;
  
  void setInt(int paramInt1, int paramInt2) throws SQLException;
  
  void setLong(int paramInt, long paramLong) throws SQLException;
  
  void setFloat(int paramInt, float paramFloat) throws SQLException;
  
  void setDouble(int paramInt, double paramDouble) throws SQLException;
  
  void setBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException;
  
  void setString(int paramInt, String paramString) throws SQLException;
  
  void setBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException;
  
  void setDate(int paramInt, Date paramDate) throws SQLException;
  
  void setTime(int paramInt, Time paramTime) throws SQLException;
  
  void setTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException;
  
  void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException;
  
  @Deprecated
  void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException;
  
  void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException;
  
  void clearParameters() throws SQLException;
  
  void setObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException;
  
  void setObject(int paramInt, Object paramObject) throws SQLException;
  
  boolean execute() throws SQLException;
  
  void addBatch() throws SQLException;
  
  void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException;
  
  void setRef(int paramInt, Ref paramRef) throws SQLException;
  
  void setBlob(int paramInt, Blob paramBlob) throws SQLException;
  
  void setClob(int paramInt, Clob paramClob) throws SQLException;
  
  void setArray(int paramInt, Array paramArray) throws SQLException;
  
  ResultSetMetaData getMetaData() throws SQLException;
  
  void setDate(int paramInt, Date paramDate, Calendar paramCalendar) throws SQLException;
  
  void setTime(int paramInt, Time paramTime, Calendar paramCalendar) throws SQLException;
  
  void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar) throws SQLException;
  
  void setNull(int paramInt1, int paramInt2, String paramString) throws SQLException;
  
  void setURL(int paramInt, URL paramURL) throws SQLException;
  
  ParameterMetaData getParameterMetaData() throws SQLException;
  
  void setRowId(int paramInt, RowId paramRowId) throws SQLException;
  
  void setNString(int paramInt, String paramString) throws SQLException;
  
  void setNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void setNClob(int paramInt, NClob paramNClob) throws SQLException;
  
  void setClob(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void setBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void setNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void setSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException;
  
  void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3) throws SQLException;
  
  void setAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void setBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void setCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void setAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException;
  
  void setBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException;
  
  void setCharacterStream(int paramInt, Reader paramReader) throws SQLException;
  
  void setNCharacterStream(int paramInt, Reader paramReader) throws SQLException;
  
  void setClob(int paramInt, Reader paramReader) throws SQLException;
  
  void setBlob(int paramInt, InputStream paramInputStream) throws SQLException;
  
  void setNClob(int paramInt, Reader paramReader) throws SQLException;
  
  default void setObject(int paramInt1, Object paramObject, SQLType paramSQLType, int paramInt2) throws SQLException { throw new SQLFeatureNotSupportedException("setObject not implemented"); }
  
  default void setObject(int paramInt, Object paramObject, SQLType paramSQLType) throws SQLException { throw new SQLFeatureNotSupportedException("setObject not implemented"); }
  
  default long executeLargeUpdate() throws SQLException { throw new UnsupportedOperationException("executeLargeUpdate not implemented"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\PreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */