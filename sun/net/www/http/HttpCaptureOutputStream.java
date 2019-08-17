package sun.net.www.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpCaptureOutputStream extends FilterOutputStream {
  private HttpCapture capture = null;
  
  public HttpCaptureOutputStream(OutputStream paramOutputStream, HttpCapture paramHttpCapture) {
    super(paramOutputStream);
    this.capture = paramHttpCapture;
  }
  
  public void write(int paramInt) throws IOException {
    this.capture.sent(paramInt);
    this.out.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte) throws IOException {
    for (byte b : paramArrayOfByte)
      this.capture.sent(b); 
    this.out.write(paramArrayOfByte);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    for (int i = paramInt1; i < paramInt2; i++)
      this.capture.sent(paramArrayOfByte[i]); 
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void flush() throws IOException {
    try {
      this.capture.flush();
    } catch (IOException iOException) {}
    super.flush();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\HttpCaptureOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */