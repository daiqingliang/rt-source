package java.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;

public interface SQLOutput {
  void writeString(String paramString) throws SQLException;
  
  void writeBoolean(boolean paramBoolean) throws SQLException;
  
  void writeByte(byte paramByte) throws SQLException;
  
  void writeShort(short paramShort) throws SQLException;
  
  void writeInt(int paramInt) throws SQLException;
  
  void writeLong(long paramLong) throws SQLException;
  
  void writeFloat(float paramFloat) throws SQLException;
  
  void writeDouble(double paramDouble) throws SQLException;
  
  void writeBigDecimal(BigDecimal paramBigDecimal) throws SQLException;
  
  void writeBytes(byte[] paramArrayOfByte) throws SQLException;
  
  void writeDate(Date paramDate) throws SQLException;
  
  void writeTime(Time paramTime) throws SQLException;
  
  void writeTimestamp(Timestamp paramTimestamp) throws SQLException;
  
  void writeCharacterStream(Reader paramReader) throws SQLException;
  
  void writeAsciiStream(InputStream paramInputStream) throws SQLException;
  
  void writeBinaryStream(InputStream paramInputStream) throws SQLException;
  
  void writeObject(SQLData paramSQLData) throws SQLException;
  
  void writeRef(Ref paramRef) throws SQLException;
  
  void writeBlob(Blob paramBlob) throws SQLException;
  
  void writeClob(Clob paramClob) throws SQLException;
  
  void writeStruct(Struct paramStruct) throws SQLException;
  
  void writeArray(Array paramArray) throws SQLException;
  
  void writeURL(URL paramURL) throws SQLException;
  
  void writeNString(String paramString) throws SQLException;
  
  void writeNClob(NClob paramNClob) throws SQLException;
  
  void writeRowId(RowId paramRowId) throws SQLException;
  
  void writeSQLXML(SQLXML paramSQLXML) throws SQLException;
  
  default void writeObject(Object paramObject, SQLType paramSQLType) throws SQLException { throw new SQLFeatureNotSupportedException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\SQLOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */