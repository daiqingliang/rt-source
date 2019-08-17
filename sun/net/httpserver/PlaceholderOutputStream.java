package sun.net.httpserver;

import java.io.IOException;
import java.io.OutputStream;

class PlaceholderOutputStream extends OutputStream {
  OutputStream wrapped;
  
  PlaceholderOutputStream(OutputStream paramOutputStream) { this.wrapped = paramOutputStream; }
  
  void setWrappedStream(OutputStream paramOutputStream) { this.wrapped = paramOutputStream; }
  
  boolean isWrapped() { return (this.wrapped != null); }
  
  private void checkWrap() throws IOException {
    if (this.wrapped == null)
      throw new IOException("response headers not sent yet"); 
  }
  
  public void write(int paramInt) throws IOException {
    checkWrap();
    this.wrapped.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte) throws IOException {
    checkWrap();
    this.wrapped.write(paramArrayOfByte);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    checkWrap();
    this.wrapped.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void flush() throws IOException {
    checkWrap();
    this.wrapped.flush();
  }
  
  public void close() throws IOException {
    checkWrap();
    this.wrapped.close();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\PlaceholderOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */