package sun.rmi.transport.proxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class HttpOutputStream extends ByteArrayOutputStream {
  protected OutputStream out;
  
  boolean responseSent = false;
  
  private static byte[] emptyData = { 0 };
  
  public HttpOutputStream(OutputStream paramOutputStream) { this.out = paramOutputStream; }
  
  public void close() throws IOException {
    if (!this.responseSent) {
      if (size() == 0)
        write(emptyData); 
      DataOutputStream dataOutputStream = new DataOutputStream(this.out);
      dataOutputStream.writeBytes("Content-type: application/octet-stream\r\n");
      dataOutputStream.writeBytes("Content-length: " + size() + "\r\n");
      dataOutputStream.writeBytes("\r\n");
      writeTo(dataOutputStream);
      dataOutputStream.flush();
      reset();
      this.responseSent = true;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\HttpOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */