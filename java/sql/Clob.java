package java.sql;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface Clob {
  long length() throws SQLException;
  
  String getSubString(long paramLong, int paramInt) throws SQLException;
  
  Reader getCharacterStream() throws SQLException;
  
  InputStream getAsciiStream() throws SQLException;
  
  long position(String paramString, long paramLong) throws SQLException;
  
  long position(Clob paramClob, long paramLong) throws SQLException;
  
  int setString(long paramLong, String paramString) throws SQLException;
  
  int setString(long paramLong, String paramString, int paramInt1, int paramInt2) throws SQLException;
  
  OutputStream setAsciiStream(long paramLong) throws SQLException;
  
  Writer setCharacterStream(long paramLong) throws SQLException;
  
  void truncate(long paramLong) throws SQLException;
  
  void free() throws SQLException;
  
  Reader getCharacterStream(long paramLong1, long paramLong2) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Clob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */