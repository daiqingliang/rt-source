package sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;

class FixedLengthInputStream extends LeftOverInputStream {
  private long remaining;
  
  FixedLengthInputStream(ExchangeImpl paramExchangeImpl, InputStream paramInputStream, long paramLong) {
    super(paramExchangeImpl, paramInputStream);
    this.remaining = paramLong;
  }
  
  protected int readImpl(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    this.eof = (this.remaining == 0L);
    if (this.eof)
      return -1; 
    if (paramInt2 > this.remaining)
      paramInt2 = (int)this.remaining; 
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i > -1) {
      this.remaining -= i;
      if (this.remaining == 0L)
        this.t.getServerImpl().requestCompleted(this.t.getConnection()); 
    } 
    return i;
  }
  
  public int available() throws IOException {
    if (this.eof)
      return 0; 
    int i = this.in.available();
    return (i < this.remaining) ? i : (int)this.remaining;
  }
  
  public boolean markSupported() { return false; }
  
  public void mark(int paramInt) {}
  
  public void reset() throws IOException { throw new IOException("mark/reset not supported"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\FixedLengthInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */