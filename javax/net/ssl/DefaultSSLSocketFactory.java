package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

class DefaultSSLSocketFactory extends SSLSocketFactory {
  private Exception reason;
  
  DefaultSSLSocketFactory(Exception paramException) { this.reason = paramException; }
  
  private Socket throwException() throws SocketException { throw (SocketException)(new SocketException(this.reason.toString())).initCause(this.reason); }
  
  public Socket createSocket() throws SocketException { return throwException(); }
  
  public Socket createSocket(String paramString, int paramInt) throws IOException { return throwException(); }
  
  public Socket createSocket(Socket paramSocket, String paramString, int paramInt, boolean paramBoolean) throws IOException { return throwException(); }
  
  public Socket createSocket(InetAddress paramInetAddress, int paramInt) throws IOException { return throwException(); }
  
  public Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException { return throwException(); }
  
  public Socket createSocket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2) throws IOException { return throwException(); }
  
  public String[] getDefaultCipherSuites() { return new String[0]; }
  
  public String[] getSupportedCipherSuites() { return new String[0]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\DefaultSSLSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */