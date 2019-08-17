package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.security.AccessController;
import java.security.PrivilegedAction;

class WrappedSocket extends Socket {
  protected Socket socket;
  
  protected InputStream in = null;
  
  protected OutputStream out = null;
  
  public WrappedSocket(Socket paramSocket, InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    super((SocketImpl)null);
    this.socket = paramSocket;
    this.in = paramInputStream;
    this.out = paramOutputStream;
  }
  
  public InetAddress getInetAddress() { return this.socket.getInetAddress(); }
  
  public InetAddress getLocalAddress() { return (InetAddress)AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
          public InetAddress run() { return WrappedSocket.this.socket.getLocalAddress(); }
        }); }
  
  public int getPort() { return this.socket.getPort(); }
  
  public int getLocalPort() { return this.socket.getLocalPort(); }
  
  public InputStream getInputStream() throws IOException {
    if (this.in == null)
      this.in = this.socket.getInputStream(); 
    return this.in;
  }
  
  public OutputStream getOutputStream() throws IOException {
    if (this.out == null)
      this.out = this.socket.getOutputStream(); 
    return this.out;
  }
  
  public void setTcpNoDelay(boolean paramBoolean) throws SocketException { this.socket.setTcpNoDelay(paramBoolean); }
  
  public boolean getTcpNoDelay() throws SocketException { return this.socket.getTcpNoDelay(); }
  
  public void setSoLinger(boolean paramBoolean, int paramInt) throws SocketException { this.socket.setSoLinger(paramBoolean, paramInt); }
  
  public int getSoLinger() { return this.socket.getSoLinger(); }
  
  public void setSoTimeout(int paramInt) throws SocketException { this.socket.setSoTimeout(paramInt); }
  
  public int getSoTimeout() { return this.socket.getSoTimeout(); }
  
  public void close() throws IOException { this.socket.close(); }
  
  public String toString() { return "Wrapped" + this.socket.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\WrappedSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */