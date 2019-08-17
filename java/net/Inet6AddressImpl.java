package java.net;

import java.io.IOException;
import java.util.Enumeration;

class Inet6AddressImpl implements InetAddressImpl {
  private InetAddress anyLocalAddress;
  
  private InetAddress loopbackAddress;
  
  public native String getLocalHostName() throws UnknownHostException;
  
  public native InetAddress[] lookupAllHostAddr(String paramString) throws UnknownHostException;
  
  public native String getHostByAddr(byte[] paramArrayOfByte) throws UnknownHostException;
  
  private native boolean isReachable0(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4) throws IOException;
  
  public boolean isReachable(InetAddress paramInetAddress, int paramInt1, NetworkInterface paramNetworkInterface, int paramInt2) throws IOException {
    byte[] arrayOfByte = null;
    int i = -1;
    int j = -1;
    if (paramNetworkInterface != null) {
      Enumeration enumeration = paramNetworkInterface.getInetAddresses();
      InetAddress inetAddress = null;
      while (enumeration.hasMoreElements()) {
        inetAddress = (InetAddress)enumeration.nextElement();
        if (inetAddress.getClass().isInstance(paramInetAddress)) {
          arrayOfByte = inetAddress.getAddress();
          if (inetAddress instanceof Inet6Address)
            j = ((Inet6Address)inetAddress).getScopeId(); 
          break;
        } 
      } 
      if (arrayOfByte == null)
        return false; 
    } 
    if (paramInetAddress instanceof Inet6Address)
      i = ((Inet6Address)paramInetAddress).getScopeId(); 
    return isReachable0(paramInetAddress.getAddress(), i, paramInt1, arrayOfByte, paramInt2, j);
  }
  
  public InetAddress anyLocalAddress() {
    if (this.anyLocalAddress == null)
      if (InetAddress.preferIPv6Address) {
        this.anyLocalAddress = new Inet6Address();
        (this.anyLocalAddress.holder()).hostName = "::";
      } else {
        this.anyLocalAddress = (new Inet4AddressImpl()).anyLocalAddress();
      }  
    return this.anyLocalAddress;
  }
  
  public InetAddress loopbackAddress() {
    if (this.loopbackAddress == null)
      if (InetAddress.preferIPv6Address) {
        byte[] arrayOfByte = { 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 1 };
        this.loopbackAddress = new Inet6Address("localhost", arrayOfByte);
      } else {
        this.loopbackAddress = (new Inet4AddressImpl()).loopbackAddress();
      }  
    return this.loopbackAddress;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\Inet6AddressImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */