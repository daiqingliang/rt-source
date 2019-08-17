package sun.rmi.transport.tcp;

import java.io.IOException;
import java.io.OutputStream;

final class MultiplexOutputStream extends OutputStream {
  private ConnectionMultiplexer manager;
  
  private MultiplexConnectionInfo info;
  
  private byte[] buffer;
  
  private int pos = 0;
  
  private int requested = 0;
  
  private boolean disconnected = false;
  
  private Object lock = new Object();
  
  MultiplexOutputStream(ConnectionMultiplexer paramConnectionMultiplexer, MultiplexConnectionInfo paramMultiplexConnectionInfo, int paramInt) {
    this.manager = paramConnectionMultiplexer;
    this.info = paramMultiplexConnectionInfo;
    this.buffer = new byte[paramInt];
    this.pos = 0;
  }
  
  public void write(int paramInt) throws IOException {
    while (this.pos >= this.buffer.length)
      push(); 
    this.buffer[this.pos++] = (byte)paramInt;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 <= 0)
      return; 
    int i = this.buffer.length - this.pos;
    if (paramInt2 <= i) {
      System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, this.pos, paramInt2);
      this.pos += paramInt2;
      return;
    } 
    flush();
    while (true) {
      int j;
      synchronized (this.lock) {
        while ((j = this.requested) < 1 && !this.disconnected) {
          try {
            this.lock.wait();
          } catch (InterruptedException interruptedException) {}
        } 
        if (this.disconnected)
          throw new IOException("Connection closed"); 
      } 
      if (j < paramInt2) {
        this.manager.sendTransmit(this.info, paramArrayOfByte, paramInt1, j);
        paramInt1 += j;
        paramInt2 -= j;
        synchronized (this.lock) {
          this.requested -= j;
          continue;
        } 
      } 
      break;
    } 
    this.manager.sendTransmit(this.info, paramArrayOfByte, paramInt1, paramInt2);
    synchronized (this.lock) {
      this.requested -= paramInt2;
    } 
  }
  
  public void flush() throws IOException {
    while (this.pos > 0)
      push(); 
  }
  
  public void close() throws IOException { this.manager.sendClose(this.info); }
  
  void request(int paramInt) throws IOException {
    synchronized (this.lock) {
      this.requested += paramInt;
      this.lock.notifyAll();
    } 
  }
  
  void disconnect() throws IOException {
    synchronized (this.lock) {
      this.disconnected = true;
      this.lock.notifyAll();
    } 
  }
  
  private void push() throws IOException {
    int i;
    synchronized (this.lock) {
      while ((i = this.requested) < 1 && !this.disconnected) {
        try {
          this.lock.wait();
        } catch (InterruptedException interruptedException) {}
      } 
      if (this.disconnected)
        throw new IOException("Connection closed"); 
    } 
    if (i < this.pos) {
      this.manager.sendTransmit(this.info, this.buffer, 0, i);
      System.arraycopy(this.buffer, i, this.buffer, 0, this.pos - i);
      this.pos -= i;
      synchronized (this.lock) {
        this.requested -= i;
      } 
    } else {
      this.manager.sendTransmit(this.info, this.buffer, 0, this.pos);
      synchronized (this.lock) {
        this.requested -= this.pos;
      } 
      this.pos = 0;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\tcp\MultiplexOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */