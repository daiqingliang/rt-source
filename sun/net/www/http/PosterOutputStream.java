package sun.net.www.http;

import java.io.ByteArrayOutputStream;

public class PosterOutputStream extends ByteArrayOutputStream {
  private boolean closed;
  
  public PosterOutputStream() { super(256); }
  
  public void write(int paramInt) {
    if (this.closed)
      return; 
    super.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (this.closed)
      return; 
    super.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void reset() {
    if (this.closed)
      return; 
    super.reset();
  }
  
  public void close() {
    this.closed = true;
    super.close();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\PosterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */