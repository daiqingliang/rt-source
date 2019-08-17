package sun.rmi.transport.proxy;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class HttpSendOutputStream extends FilterOutputStream {
  HttpSendSocket owner;
  
  public HttpSendOutputStream(OutputStream paramOutputStream, HttpSendSocket paramHttpSendSocket) throws IOException {
    super(paramOutputStream);
    this.owner = paramHttpSendSocket;
  }
  
  public void deactivate() { this.out = null; }
  
  public void write(int paramInt) throws IOException {
    if (this.out == null)
      this.out = this.owner.writeNotify(); 
    this.out.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 == 0)
      return; 
    if (this.out == null)
      this.out = this.owner.writeNotify(); 
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void flush() {
    if (this.out != null)
      this.out.flush(); 
  }
  
  public void close() {
    flush();
    this.owner.close();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\HttpSendOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */