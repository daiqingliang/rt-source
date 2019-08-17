package sun.net.www.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpCaptureInputStream extends FilterInputStream {
  private HttpCapture capture = null;
  
  public HttpCaptureInputStream(InputStream paramInputStream, HttpCapture paramHttpCapture) {
    super(paramInputStream);
    this.capture = paramHttpCapture;
  }
  
  public int read() throws IOException {
    int i = super.read();
    this.capture.received(i);
    return i;
  }
  
  public void close() throws IOException {
    try {
      this.capture.flush();
    } catch (IOException iOException) {}
    super.close();
  }
  
  public int read(byte[] paramArrayOfByte) throws IOException {
    int i = super.read(paramArrayOfByte);
    for (byte b = 0; b < i; b++)
      this.capture.received(paramArrayOfByte[b]); 
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
    for (int j = 0; j < i; j++)
      this.capture.received(paramArrayOfByte[paramInt1 + j]); 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\HttpCaptureInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */