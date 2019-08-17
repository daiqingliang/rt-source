package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.util.Enumeration;

public interface InetAddressAcl {
  String getName();
  
  boolean checkReadPermission(InetAddress paramInetAddress);
  
  boolean checkReadPermission(InetAddress paramInetAddress, String paramString);
  
  boolean checkCommunity(String paramString);
  
  boolean checkWritePermission(InetAddress paramInetAddress);
  
  boolean checkWritePermission(InetAddress paramInetAddress, String paramString);
  
  Enumeration<InetAddress> getTrapDestinations();
  
  Enumeration<String> getTrapCommunities(InetAddress paramInetAddress);
  
  Enumeration<InetAddress> getInformDestinations();
  
  Enumeration<String> getInformCommunities(InetAddress paramInetAddress);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\InetAddressAcl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */