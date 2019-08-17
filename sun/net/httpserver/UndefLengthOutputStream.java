package sun.net.httpserver;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class UndefLengthOutputStream extends FilterOutputStream {
  private boolean closed = false;
  
  ExchangeImpl t;
  
  UndefLengthOutputStream(ExchangeImpl paramExchangeImpl, OutputStream paramOutputStream) {
    super(paramOutputStream);
    this.t = paramExchangeImpl;
  }
  
  public void write(int paramInt) throws IOException {
    if (this.closed)
      throw new IOException("stream closed"); 
    this.out.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.closed)
      throw new IOException("stream closed"); 
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void close() throws IOException {
    if (this.closed)
      return; 
    this.closed = true;
    flush();
    LeftOverInputStream leftOverInputStream = this.t.getOriginalInputStream();
    if (!leftOverInputStream.isClosed())
      try {
        leftOverInputStream.close();
      } catch (IOException iOException) {} 
    WriteFinishedEvent writeFinishedEvent = new WriteFinishedEvent(this.t);
    this.t.getHttpContext().getServerImpl().addEvent(writeFinishedEvent);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\UndefLengthOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */