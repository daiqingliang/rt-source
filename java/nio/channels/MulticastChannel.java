package java.nio.channels;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;

public interface MulticastChannel extends NetworkChannel {
  void close() throws IOException;
  
  MembershipKey join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  MembershipKey join(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\MulticastChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */