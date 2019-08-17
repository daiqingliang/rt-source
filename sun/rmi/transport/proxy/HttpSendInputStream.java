package sun.rmi.transport.proxy;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class HttpSendInputStream extends FilterInputStream {
  HttpSendSocket owner;
  
  public HttpSendInputStream(InputStream paramInputStream, HttpSendSocket paramHttpSendSocket) throws IOException {
    super(paramInputStream);
    this.owner = paramHttpSendSocket;
  }
  
  public void deactivate() { this.in = null; }
  
  public int read() throws IOException {
    if (this.in == null)
      this.in = this.owner.readNotify(); 
    return this.in.read();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 == 0)
      return 0; 
    if (this.in == null)
      this.in = this.owner.readNotify(); 
    return this.in.read(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong == 0L)
      return 0L; 
    if (this.in == null)
      this.in = this.owner.readNotify(); 
    return this.in.skip(paramLong);
  }
  
  public int available() throws IOException {
    if (this.in == null)
      this.in = this.owner.readNotify(); 
    return this.in.available();
  }
  
  public void close() { this.owner.close(); }
  
  public void mark(int paramInt) {
    if (this.in == null)
      try {
        this.in = this.owner.readNotify();
      } catch (IOException iOException) {
        return;
      }  
    this.in.mark(paramInt);
  }
  
  public void reset() {
    if (this.in == null)
      this.in = this.owner.readNotify(); 
    this.in.reset();
  }
  
  public boolean markSupported() {
    if (this.in == null)
      try {
        this.in = this.owner.readNotify();
      } catch (IOException iOException) {
        return false;
      }  
    return this.in.markSupported();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\HttpSendInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */