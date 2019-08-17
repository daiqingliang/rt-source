package sun.rmi.transport.proxy;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.rmi.runtime.Log;

class HttpInputStream extends FilterInputStream {
  protected int bytesLeft;
  
  protected int bytesLeftAtMark;
  
  public HttpInputStream(InputStream paramInputStream) throws IOException {
    super(paramInputStream);
    if (paramInputStream.markSupported())
      paramInputStream.mark(0); 
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    String str1 = "Content-length:".toLowerCase();
    boolean bool = false;
    do {
      str2 = dataInputStream.readLine();
      if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE))
        RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "received header line: \"" + str2 + "\""); 
      if (str2 == null)
        throw new EOFException(); 
      if (!str2.toLowerCase().startsWith(str1))
        continue; 
      if (bool)
        throw new IOException("Multiple Content-length entries found."); 
      this.bytesLeft = Integer.parseInt(str2.substring(str1.length()).trim());
      bool = true;
    } while (str2.length() != 0 && str2.charAt(0) != '\r' && str2.charAt(0) != '\n');
    if (!bool || this.bytesLeft < 0)
      this.bytesLeft = Integer.MAX_VALUE; 
    this.bytesLeftAtMark = this.bytesLeft;
    if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE))
      RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "content length: " + this.bytesLeft); 
  }
  
  public int available() throws IOException {
    int i = this.in.available();
    if (i > this.bytesLeft)
      i = this.bytesLeft; 
    return i;
  }
  
  public int read() throws IOException {
    if (this.bytesLeft > 0) {
      int i = this.in.read();
      if (i != -1)
        this.bytesLeft--; 
      if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE))
        RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "received byte: '" + (((i & 0x7F) < 32) ? " " : String.valueOf((char)i)) + "' " + i); 
      return i;
    } 
    RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read past content length");
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.bytesLeft == 0 && paramInt2 > 0) {
      RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read past content length");
      return -1;
    } 
    if (paramInt2 > this.bytesLeft)
      paramInt2 = this.bytesLeft; 
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    this.bytesLeft -= i;
    if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE))
      RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read " + i + " bytes, " + this.bytesLeft + " remaining"); 
    return i;
  }
  
  public void mark(int paramInt) {
    this.in.mark(paramInt);
    if (this.in.markSupported())
      this.bytesLeftAtMark = this.bytesLeft; 
  }
  
  public void reset() throws IOException {
    this.in.reset();
    this.bytesLeft = this.bytesLeftAtMark;
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong > this.bytesLeft)
      paramLong = this.bytesLeft; 
    long l = this.in.skip(paramLong);
    this.bytesLeft = (int)(this.bytesLeft - l);
    return l;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\HttpInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */