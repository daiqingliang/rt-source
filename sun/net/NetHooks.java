package sun.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;

public final class NetHooks {
  public static void beforeTcpBind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt) throws IOException {}
  
  public static void beforeTcpConnect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt) throws IOException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\NetHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */