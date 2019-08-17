package javax.sql.rowset.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

public class SQLOutputImpl implements SQLOutput {
  private Vector attribs;
  
  private Map map;
  
  public SQLOutputImpl(Vector<?> paramVector, Map<String, ?> paramMap) throws SQLException {
    if (paramVector == null || paramMap == null)
      throw new SQLException("Cannot instantiate a SQLOutputImpl instance with null parameters"); 
    this.attribs = paramVector;
    this.map = paramMap;
  }
  
  public void writeString(String paramString) throws SQLException { this.attribs.add(paramString); }
  
  public void writeBoolean(boolean paramBoolean) throws SQLException { this.attribs.add(Boolean.valueOf(paramBoolean)); }
  
  public void writeByte(byte paramByte) throws SQLException { this.attribs.add(Byte.valueOf(paramByte)); }
  
  public void writeShort(short paramShort) throws SQLException { this.attribs.add(Short.valueOf(paramShort)); }
  
  public void writeInt(int paramInt) throws SQLException { this.attribs.add(Integer.valueOf(paramInt)); }
  
  public void writeLong(long paramLong) throws SQLException { this.attribs.add(Long.valueOf(paramLong)); }
  
  public void writeFloat(float paramFloat) throws SQLException { this.attribs.add(Float.valueOf(paramFloat)); }
  
  public void writeDouble(double paramDouble) throws SQLException { this.attribs.add(Double.valueOf(paramDouble)); }
  
  public void writeBigDecimal(BigDecimal paramBigDecimal) throws SQLException { this.attribs.add(paramBigDecimal); }
  
  public void writeBytes(byte[] paramArrayOfByte) throws SQLException { this.attribs.add(paramArrayOfByte); }
  
  public void writeDate(Date paramDate) throws SQLException { this.attribs.add(paramDate); }
  
  public void writeTime(Time paramTime) throws SQLException { this.attribs.add(paramTime); }
  
  public void writeTimestamp(Timestamp paramTimestamp) throws SQLException { this.attribs.add(paramTimestamp); }
  
  public void writeCharacterStream(Reader paramReader) throws SQLException {
    BufferedReader bufferedReader = new BufferedReader(paramReader);
    try {
      int i;
      while ((i = bufferedReader.read()) != -1) {
        char c = (char)i;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(c);
        String str1 = new String(stringBuffer);
        String str2 = bufferedReader.readLine();
        writeString(str1.concat(str2));
      } 
    } catch (IOException iOException) {}
  }
  
  public void writeAsciiStream(InputStream paramInputStream) throws SQLException {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    try {
      int i;
      while ((i = bufferedReader.read()) != -1) {
        char c = (char)i;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(c);
        String str1 = new String(stringBuffer);
        String str2 = bufferedReader.readLine();
        writeString(str1.concat(str2));
      } 
    } catch (IOException iOException) {
      throw new SQLException(iOException.getMessage());
    } 
  }
  
  public void writeBinaryStream(InputStream paramInputStream) throws SQLException {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    try {
      int i;
      while ((i = bufferedReader.read()) != -1) {
        char c = (char)i;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(c);
        String str1 = new String(stringBuffer);
        String str2 = bufferedReader.readLine();
        writeString(str1.concat(str2));
      } 
    } catch (IOException iOException) {
      throw new SQLException(iOException.getMessage());
    } 
  }
  
  public void writeObject(SQLData paramSQLData) throws SQLException {
    if (paramSQLData == null) {
      this.attribs.add(null);
    } else {
      this.attribs.add(new SerialStruct(paramSQLData, this.map));
    } 
  }
  
  public void writeRef(Ref paramRef) throws SQLException {
    if (paramRef == null) {
      this.attribs.add(null);
    } else {
      this.attribs.add(new SerialRef(paramRef));
    } 
  }
  
  public void writeBlob(Blob paramBlob) throws SQLException {
    if (paramBlob == null) {
      this.attribs.add(null);
    } else {
      this.attribs.add(new SerialBlob(paramBlob));
    } 
  }
  
  public void writeClob(Clob paramClob) throws SQLException {
    if (paramClob == null) {
      this.attribs.add(null);
    } else {
      this.attribs.add(new SerialClob(paramClob));
    } 
  }
  
  public void writeStruct(Struct paramStruct) throws SQLException {
    SerialStruct serialStruct = new SerialStruct(paramStruct, this.map);
    this.attribs.add(serialStruct);
  }
  
  public void writeArray(Array paramArray) throws SQLException {
    if (paramArray == null) {
      this.attribs.add(null);
    } else {
      this.attribs.add(new SerialArray(paramArray, this.map));
    } 
  }
  
  public void writeURL(URL paramURL) throws SQLException {
    if (paramURL == null) {
      this.attribs.add(null);
    } else {
      this.attribs.add(new SerialDatalink(paramURL));
    } 
  }
  
  public void writeNString(String paramString) throws SQLException { this.attribs.add(paramString); }
  
  public void writeNClob(NClob paramNClob) throws SQLException { this.attribs.add(paramNClob); }
  
  public void writeRowId(RowId paramRowId) throws SQLException { this.attribs.add(paramRowId); }
  
  public void writeSQLXML(SQLXML paramSQLXML) throws SQLException { this.attribs.add(paramSQLXML); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\serial\SQLOutputImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */