package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;

public interface SnmpTableHandler {
  Object getData(SnmpOid paramSnmpOid);
  
  SnmpOid getNext(SnmpOid paramSnmpOid);
  
  boolean contains(SnmpOid paramSnmpOid);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snm\\util\SnmpTableHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */