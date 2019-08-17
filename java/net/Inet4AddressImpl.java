package java.net;

import java.io.IOException;
import java.util.Enumeration;

class Inet4AddressImpl implements InetAddressImpl {
  private InetAddress anyLocalAddress;
  
  private InetAddress loopbackAddress;
  
  public native String getLocalHostName() throws UnknownHostException;
  
  public native InetAddress[] lookupAllHostAddr(String paramString) throws UnknownHostException;
  
  public native String getHostByAddr(byte[] paramArrayOfByte) throws UnknownHostException;
  
  private native boolean isReachable0(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2) throws IOException;
  
  public InetAddress anyLocalAddress() {
    if (this.anyLocalAddress == null) {
      this.anyLocalAddress = new Inet4Address();
      (this.anyLocalAddress.holder()).hostName = "0.0.0.0";
    } 
    return this.anyLocalAddress;
  }
  
  public InetAddress loopbackAddress() {
    if (this.loopbackAddress == null) {
      byte[] arrayOfByte = { Byte.MAX_VALUE, 0, 0, 1 };
      this.loopbackAddress = new Inet4Address("localhost", arrayOfByte);
    } 
    return this.loopbackAddress;
  }
  
  public boolean isReachable(InetAddress paramInetAddress, int paramInt1, NetworkInterface paramNetworkInterface, int paramInt2) throws IOException {
    byte[] arrayOfByte = null;
    if (paramNetworkInterface != null) {
      Enumeration enumeration = paramNetworkInterface.getInetAddresses();
      InetAddress inetAddress;
      for (inetAddress = null; !(inetAddress instanceof Inet4Address) && enumeration.hasMoreElements(); inetAddress = (InetAddress)enumeration.nextElement());
      if (inetAddress instanceof Inet4Address)
        arrayOfByte = inetAddress.getAddress(); 
    } 
    return isReachable0(paramInetAddress.getAddress(), paramInt1, arrayOfByte, paramInt2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\Inet4AddressImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */