package sun.management.snmp.jvminstr;

import java.net.InetAddress;

public interface NotificationTarget {
  InetAddress getAddress();
  
  int getPort();
  
  String getCommunity();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\NotificationTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */