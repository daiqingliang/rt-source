package sun.rmi.log;

import java.io.IOException;
import java.io.InputStream;

public class LogInputStream extends InputStream {
  private InputStream in;
  
  private int length;
  
  public LogInputStream(InputStream paramInputStream, int paramInt) throws IOException {
    this.in = paramInputStream;
    this.length = paramInt;
  }
  
  public int read() throws IOException {
    if (this.length == 0)
      return -1; 
    int i = this.in.read();
    this.length = (i != -1) ? (this.length - 1) : 0;
    return i;
  }
  
  public int read(byte[] paramArrayOfByte) throws IOException { return read(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.length == 0)
      return -1; 
    paramInt2 = (this.length < paramInt2) ? this.length : paramInt2;
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    this.length = (i != -1) ? (this.length - i) : 0;
    return i;
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong > 2147483647L)
      throw new IOException("Too many bytes to skip - " + paramLong); 
    if (this.length == 0)
      return 0L; 
    paramLong = (this.length < paramLong) ? this.length : paramLong;
    paramLong = this.in.skip(paramLong);
    this.length = (int)(this.length - paramLong);
    return paramLong;
  }
  
  public int available() throws IOException {
    int i = this.in.available();
    return (this.length < i) ? this.length : i;
  }
  
  public void close() { this.length = 0; }
  
  protected void finalize() { close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\log\LogInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */