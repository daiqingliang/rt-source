package javax.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

class DefaultSocketFactory extends SocketFactory {
  public Socket createSocket() { return new Socket(); }
  
  public Socket createSocket(String paramString, int paramInt) throws IOException, UnknownHostException { return new Socket(paramString, paramInt); }
  
  public Socket createSocket(InetAddress paramInetAddress, int paramInt) throws IOException { return new Socket(paramInetAddress, paramInt); }
  
  public Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException, UnknownHostException { return new Socket(paramString, paramInt1, paramInetAddress, paramInt2); }
  
  public Socket createSocket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2) throws IOException { return new Socket(paramInetAddress1, paramInt1, paramInetAddress2, paramInt2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\DefaultSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */