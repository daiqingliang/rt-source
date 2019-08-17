package sun.net.httpserver;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class FixedLengthOutputStream extends FilterOutputStream {
  private long remaining;
  
  private boolean eof = false;
  
  private boolean closed = false;
  
  ExchangeImpl t;
  
  FixedLengthOutputStream(ExchangeImpl paramExchangeImpl, OutputStream paramOutputStream, long paramLong) {
    super(paramOutputStream);
    this.t = paramExchangeImpl;
    this.remaining = paramLong;
  }
  
  public void write(int paramInt) throws IOException {
    if (this.closed)
      throw new IOException("stream closed"); 
    this.eof = (this.remaining == 0L);
    if (this.eof)
      throw new StreamClosedException(); 
    this.out.write(paramInt);
    this.remaining--;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.closed)
      throw new IOException("stream closed"); 
    this.eof = (this.remaining == 0L);
    if (this.eof)
      throw new StreamClosedException(); 
    if (paramInt2 > this.remaining)
      throw new IOException("too many bytes to write to stream"); 
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
    this.remaining -= paramInt2;
  }
  
  public void close() throws IOException {
    if (this.closed)
      return; 
    this.closed = true;
    if (this.remaining > 0L) {
      this.t.close();
      throw new IOException("insufficient bytes written to stream");
    } 
    flush();
    this.eof = true;
    LeftOverInputStream leftOverInputStream = this.t.getOriginalInputStream();
    if (!leftOverInputStream.isClosed())
      try {
        leftOverInputStream.close();
      } catch (IOException iOException) {} 
    WriteFinishedEvent writeFinishedEvent = new WriteFinishedEvent(this.t);
    this.t.getHttpContext().getServerImpl().addEvent(writeFinishedEvent);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\FixedLengthOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */