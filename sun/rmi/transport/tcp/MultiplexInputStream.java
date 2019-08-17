package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class MultiplexInputStream extends InputStream {
  private ConnectionMultiplexer manager;
  
  private MultiplexConnectionInfo info;
  
  private byte[] buffer;
  
  private int present = 0;
  
  private int pos = 0;
  
  private int requested = 0;
  
  private boolean disconnected = false;
  
  private Object lock = new Object();
  
  private int waterMark;
  
  private byte[] temp = new byte[1];
  
  MultiplexInputStream(ConnectionMultiplexer paramConnectionMultiplexer, MultiplexConnectionInfo paramMultiplexConnectionInfo, int paramInt) {
    this.manager = paramConnectionMultiplexer;
    this.info = paramMultiplexConnectionInfo;
    this.buffer = new byte[paramInt];
    this.waterMark = paramInt / 2;
  }
  
  public int read() throws IOException {
    int i = read(this.temp, 0, 1);
    return (i != 1) ? -1 : (this.temp[0] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i;
    if (paramInt2 <= 0)
      return 0; 
    synchronized (this.lock) {
      if (this.pos >= this.present) {
        this.pos = this.present = 0;
      } else if (this.pos >= this.waterMark) {
        System.arraycopy(this.buffer, this.pos, this.buffer, 0, this.present - this.pos);
        this.present -= this.pos;
        this.pos = 0;
      } 
      int j = this.buffer.length - this.present;
      i = Math.max(j - this.requested, 0);
    } 
    if (i > 0)
      this.manager.sendRequest(this.info, i); 
    synchronized (this.lock) {
      this.requested += i;
      while (this.pos >= this.present && !this.disconnected) {
        try {
          this.lock.wait();
        } catch (InterruptedException interruptedException) {}
      } 
      if (this.disconnected && this.pos >= this.present)
        return -1; 
      int j = this.present - this.pos;
      if (paramInt2 < j) {
        System.arraycopy(this.buffer, this.pos, paramArrayOfByte, paramInt1, paramInt2);
        this.pos += paramInt2;
        return paramInt2;
      } 
      System.arraycopy(this.buffer, this.pos, paramArrayOfByte, paramInt1, j);
      this.pos = this.present = 0;
      return j;
    } 
  }
  
  public int available() throws IOException {
    synchronized (this.lock) {
      return this.present - this.pos;
    } 
  }
  
  public void close() throws IOException { this.manager.sendClose(this.info); }
  
  void receive(int paramInt, DataInputStream paramDataInputStream) throws IOException {
    synchronized (this.lock) {
      if (this.pos > 0 && this.buffer.length - this.present < paramInt) {
        System.arraycopy(this.buffer, this.pos, this.buffer, 0, this.present - this.pos);
        this.present -= this.pos;
        this.pos = 0;
      } 
      if (this.buffer.length - this.present < paramInt)
        throw new IOException("Receive buffer overflow"); 
      paramDataInputStream.readFully(this.buffer, this.present, paramInt);
      this.present += paramInt;
      this.requested -= paramInt;
      this.lock.notifyAll();
    } 
  }
  
  void disconnect() throws IOException {
    synchronized (this.lock) {
      this.disconnected = true;
      this.lock.notifyAll();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\tcp\MultiplexInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */