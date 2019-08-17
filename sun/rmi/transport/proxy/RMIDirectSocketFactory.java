package sun.rmi.transport.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

public class RMIDirectSocketFactory extends RMISocketFactory {
  public Socket createSocket(String paramString, int paramInt) throws IOException { return new Socket(paramString, paramInt); }
  
  public ServerSocket createServerSocket(int paramInt) throws IOException { return new ServerSocket(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\RMIDirectSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */