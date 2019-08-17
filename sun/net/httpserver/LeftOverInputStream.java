package sun.net.httpserver;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

abstract class LeftOverInputStream extends FilterInputStream {
  ExchangeImpl t;
  
  ServerImpl server;
  
  protected boolean closed = false;
  
  protected boolean eof = false;
  
  byte[] one = new byte[1];
  
  public LeftOverInputStream(ExchangeImpl paramExchangeImpl, InputStream paramInputStream) {
    super(paramInputStream);
    this.t = paramExchangeImpl;
    this.server = paramExchangeImpl.getServerImpl();
  }
  
  public boolean isDataBuffered() throws IOException {
    assert this.eof;
    return (available() > 0);
  }
  
  public void close() throws IOException {
    if (this.closed)
      return; 
    this.closed = true;
    if (!this.eof)
      this.eof = drain(ServerConfig.getDrainAmount()); 
  }
  
  public boolean isClosed() throws IOException { return this.closed; }
  
  public boolean isEOF() throws IOException { return this.eof; }
  
  protected abstract int readImpl(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  public int read() throws IOException {
    if (this.closed)
      throw new IOException("Stream is closed"); 
    int i = readImpl(this.one, 0, 1);
    return (i == -1 || i == 0) ? i : (this.one[0] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.closed)
      throw new IOException("Stream is closed"); 
    return readImpl(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public boolean drain(long paramLong) throws IOException {
    char c = 'ࠀ';
    byte[] arrayOfByte = new byte[c];
    while (paramLong > 0L) {
      long l = readImpl(arrayOfByte, 0, c);
      if (l == -1L) {
        this.eof = true;
        return true;
      } 
      paramLong -= l;
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\LeftOverInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */