package sun.rmi.transport.proxy;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class HttpReceiveSocket extends WrappedSocket implements RMISocketInfo {
  private boolean headerSent = false;
  
  public HttpReceiveSocket(Socket paramSocket, InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    super(paramSocket, paramInputStream, paramOutputStream);
    this.in = new HttpInputStream((paramInputStream != null) ? paramInputStream : paramSocket.getInputStream());
    this.out = (paramOutputStream != null) ? paramOutputStream : paramSocket.getOutputStream();
  }
  
  public boolean isReusable() { return false; }
  
  public InetAddress getInetAddress() { return null; }
  
  public OutputStream getOutputStream() throws IOException {
    if (!this.headerSent) {
      DataOutputStream dataOutputStream = new DataOutputStream(this.out);
      dataOutputStream.writeBytes("HTTP/1.0 200 OK\r\n");
      dataOutputStream.flush();
      this.headerSent = true;
      this.out = new HttpOutputStream(this.out);
    } 
    return this.out;
  }
  
  public void close() throws IOException {
    getOutputStream().close();
    this.socket.close();
  }
  
  public String toString() { return "HttpReceive" + this.socket.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\HttpReceiveSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */