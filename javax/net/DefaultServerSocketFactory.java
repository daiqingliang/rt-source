package javax.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

class DefaultServerSocketFactory extends ServerSocketFactory {
  public ServerSocket createServerSocket() throws IOException { return new ServerSocket(); }
  
  public ServerSocket createServerSocket(int paramInt) throws IOException { return new ServerSocket(paramInt); }
  
  public ServerSocket createServerSocket(int paramInt1, int paramInt2) throws IOException { return new ServerSocket(paramInt1, paramInt2); }
  
  public ServerSocket createServerSocket(int paramInt1, int paramInt2, InetAddress paramInetAddress) throws IOException { return new ServerSocket(paramInt1, paramInt2, paramInetAddress); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\DefaultServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */