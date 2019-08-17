package javax.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public interface RowSet extends ResultSet {
  String getUrl() throws SQLException;
  
  void setUrl(String paramString) throws SQLException;
  
  String getDataSourceName() throws SQLException;
  
  void setDataSourceName(String paramString) throws SQLException;
  
  String getUsername() throws SQLException;
  
  void setUsername(String paramString) throws SQLException;
  
  String getPassword() throws SQLException;
  
  void setPassword(String paramString) throws SQLException;
  
  int getTransactionIsolation();
  
  void setTransactionIsolation(int paramInt) throws SQLException;
  
  Map<String, Class<?>> getTypeMap() throws SQLException;
  
  void setTypeMap(Map<String, Class<?>> paramMap) throws SQLException;
  
  String getCommand() throws SQLException;
  
  void setCommand(String paramString) throws SQLException;
  
  boolean isReadOnly();
  
  void setReadOnly(boolean paramBoolean) throws SQLException;
  
  int getMaxFieldSize();
  
  void setMaxFieldSize(int paramInt) throws SQLException;
  
  int getMaxRows();
  
  void setMaxRows(int paramInt) throws SQLException;
  
  boolean getEscapeProcessing();
  
  void setEscapeProcessing(boolean paramBoolean) throws SQLException;
  
  int getQueryTimeout();
  
  void setQueryTimeout(int paramInt) throws SQLException;
  
  void setType(int paramInt) throws SQLException;
  
  void setConcurrency(int paramInt) throws SQLException;
  
  void setNull(int paramInt1, int paramInt2) throws SQLException;
  
  void setNull(String paramString, int paramInt) throws SQLException;
  
  void setNull(int paramInt1, int paramInt2, String paramString) throws SQLException;
  
  void setNull(String paramString1, int paramInt, String paramString2) throws SQLException;
  
  void setBoolean(int paramInt, boolean paramBoolean) throws SQLException;
  
  void setBoolean(String paramString, boolean paramBoolean) throws SQLException;
  
  void setByte(int paramInt, byte paramByte) throws SQLException;
  
  void setByte(String paramString, byte paramByte) throws SQLException;
  
  void setShort(int paramInt, short paramShort) throws SQLException;
  
  void setShort(String paramString, short paramShort) throws SQLException;
  
  void setInt(int paramInt1, int paramInt2) throws SQLException;
  
  void setInt(String paramString, int paramInt) throws SQLException;
  
  void setLong(int paramInt, long paramLong) throws SQLException;
  
  void setLong(String paramString, long paramLong) throws SQLException;
  
  void setFloat(int paramInt, float paramFloat) throws SQLException;
  
  void setFloat(String paramString, float paramFloat) throws SQLException;
  
  void setDouble(int paramInt, double paramDouble) throws SQLException;
  
  void setDouble(String paramString, double paramDouble) throws SQLException;
  
  void setBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException;
  
  void setBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException;
  
  void setString(int paramInt, String paramString) throws SQLException;
  
  void setString(String paramString1, String paramString2) throws SQLException;
  
  void setBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException;
  
  void setBytes(String paramString, byte[] paramArrayOfByte) throws SQLException;
  
  void setDate(int paramInt, Date paramDate) throws SQLException;
  
  void setTime(int paramInt, Time paramTime) throws SQLException;
  
  void setTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException;
  
  void setTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException;
  
  void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException;
  
  void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException;
  
  void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException;
  
  void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException;
  
  void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException;
  
  void setCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException;
  
  void setAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException;
  
  void setAsciiStream(String paramString, InputStream paramInputStream) throws SQLException;
  
  void setBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException;
  
  void setBinaryStream(String paramString, InputStream paramInputStream) throws SQLException;
  
  void setCharacterStream(int paramInt, Reader paramReader) throws SQLException;
  
  void setCharacterStream(String paramString, Reader paramReader) throws SQLException;
  
  void setNCharacterStream(int paramInt, Reader paramReader) throws SQLException;
  
  void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3) throws SQLException;
  
  void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2) throws SQLException;
  
  void setObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException;
  
  void setObject(String paramString, Object paramObject, int paramInt) throws SQLException;
  
  void setObject(String paramString, Object paramObject) throws SQLException;
  
  void setObject(int paramInt, Object paramObject) throws SQLException;
  
  void setRef(int paramInt, Ref paramRef) throws SQLException;
  
  void setBlob(int paramInt, Blob paramBlob) throws SQLException;
  
  void setBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void setBlob(int paramInt, InputStream paramInputStream) throws SQLException;
  
  void setBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException;
  
  void setBlob(String paramString, Blob paramBlob) throws SQLException;
  
  void setBlob(String paramString, InputStream paramInputStream) throws SQLException;
  
  void setClob(int paramInt, Clob paramClob) throws SQLException;
  
  void setClob(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void setClob(int paramInt, Reader paramReader) throws SQLException;
  
  void setClob(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void setClob(String paramString, Clob paramClob) throws SQLException;
  
  void setClob(String paramString, Reader paramReader) throws SQLException;
  
  void setArray(int paramInt, Array paramArray) throws SQLException;
  
  void setDate(int paramInt, Date paramDate, Calendar paramCalendar) throws SQLException;
  
  void setDate(String paramString, Date paramDate) throws SQLException;
  
  void setDate(String paramString, Date paramDate, Calendar paramCalendar) throws SQLException;
  
  void setTime(int paramInt, Time paramTime, Calendar paramCalendar) throws SQLException;
  
  void setTime(String paramString, Time paramTime) throws SQLException;
  
  void setTime(String paramString, Time paramTime, Calendar paramCalendar) throws SQLException;
  
  void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar) throws SQLException;
  
  void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar) throws SQLException;
  
  void clearParameters() throws SQLException;
  
  void execute() throws SQLException;
  
  void addRowSetListener(RowSetListener paramRowSetListener);
  
  void removeRowSetListener(RowSetListener paramRowSetListener);
  
  void setSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException;
  
  void setSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException;
  
  void setRowId(int paramInt, RowId paramRowId) throws SQLException;
  
  void setRowId(String paramString, RowId paramRowId) throws SQLException;
  
  void setNString(int paramInt, String paramString) throws SQLException;
  
  void setNString(String paramString1, String paramString2) throws SQLException;
  
  void setNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void setNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void setNCharacterStream(String paramString, Reader paramReader) throws SQLException;
  
  void setNClob(String paramString, NClob paramNClob) throws SQLException;
  
  void setNClob(String paramString, Reader paramReader, long paramLong) throws SQLException;
  
  void setNClob(String paramString, Reader paramReader) throws SQLException;
  
  void setNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException;
  
  void setNClob(int paramInt, NClob paramNClob) throws SQLException;
  
  void setNClob(int paramInt, Reader paramReader) throws SQLException;
  
  void setURL(int paramInt, URL paramURL) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\RowSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */