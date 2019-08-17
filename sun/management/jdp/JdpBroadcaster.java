package sun.management.jdp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.UnsupportedAddressTypeException;

public final class JdpBroadcaster {
  private final InetAddress addr;
  
  private final int port;
  
  private final DatagramChannel channel;
  
  public JdpBroadcaster(InetAddress paramInetAddress1, InetAddress paramInetAddress2, int paramInt1, int paramInt2) throws IOException, JdpException {
    this.addr = paramInetAddress1;
    this.port = paramInt1;
    StandardProtocolFamily standardProtocolFamily = (paramInetAddress1 instanceof java.net.Inet6Address) ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
    this.channel = DatagramChannel.open(standardProtocolFamily);
    this.channel.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.valueOf(true));
    this.channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, Integer.valueOf(paramInt2));
    if (paramInetAddress2 != null) {
      NetworkInterface networkInterface = NetworkInterface.getByInetAddress(paramInetAddress2);
      try {
        this.channel.bind(new InetSocketAddress(paramInetAddress2, 0));
      } catch (UnsupportedAddressTypeException unsupportedAddressTypeException) {
        throw new JdpException("Unable to bind to source address");
      } 
      this.channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
    } 
  }
  
  public JdpBroadcaster(InetAddress paramInetAddress, int paramInt1, int paramInt2) throws IOException, JdpException { this(paramInetAddress, null, paramInt1, paramInt2); }
  
  public void sendPacket(JdpPacket paramJdpPacket) throws IOException {
    byte[] arrayOfByte = paramJdpPacket.getPacketData();
    ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte);
    this.channel.send(byteBuffer, new InetSocketAddress(this.addr, this.port));
  }
  
  public void shutdown() throws IOException { this.channel.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\jdp\JdpBroadcaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */