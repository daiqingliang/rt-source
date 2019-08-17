package java.net;

class InetAddressImplFactory {
  static InetAddressImpl create() { return InetAddress.loadImpl(isIPv6Supported() ? "Inet6AddressImpl" : "Inet4AddressImpl"); }
  
  static native boolean isIPv6Supported();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\InetAddressImplFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */