package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

class DefaultSSLServerSocketFactory extends SSLServerSocketFactory {
  private final Exception reason;
  
  DefaultSSLServerSocketFactory(Exception paramException) { this.reason = paramException; }
  
  private ServerSocket throwException() throws SocketException { throw (SocketException)(new SocketException(this.reason.toString())).initCause(this.reason); }
  
  public ServerSocket createServerSocket() throws SocketException { return throwException(); }
  
  public ServerSocket createServerSocket(int paramInt) throws IOException { return throwException(); }
  
  public ServerSocket createServerSocket(int paramInt1, int paramInt2) throws IOException { return throwException(); }
  
  public ServerSocket createServerSocket(int paramInt1, int paramInt2, InetAddress paramInetAddress) throws IOException { return throwException(); }
  
  public String[] getDefaultCipherSuites() { return new String[0]; }
  
  public String[] getSupportedCipherSuites() { return new String[0]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\DefaultSSLServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */