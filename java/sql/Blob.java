package java.sql;

import java.io.InputStream;
import java.io.OutputStream;

public interface Blob {
  long length() throws SQLException;
  
  byte[] getBytes(long paramLong, int paramInt) throws SQLException;
  
  InputStream getBinaryStream() throws SQLException;
  
  long position(byte[] paramArrayOfByte, long paramLong) throws SQLException;
  
  long position(Blob paramBlob, long paramLong) throws SQLException;
  
  int setBytes(long paramLong, byte[] paramArrayOfByte) throws SQLException;
  
  int setBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SQLException;
  
  OutputStream setBinaryStream(long paramLong) throws SQLException;
  
  void truncate(long paramLong) throws SQLException;
  
  void free() throws SQLException;
  
  InputStream getBinaryStream(long paramLong1, long paramLong2) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Blob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */