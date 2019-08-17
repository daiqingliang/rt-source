package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import java.util.TreeMap;

public final class SnmpLoadedClassData extends SnmpCachedData {
  public SnmpLoadedClassData(long paramLong, TreeMap<SnmpOid, Object> paramTreeMap) { super(paramLong, paramTreeMap, false); }
  
  public final Object getData(SnmpOid paramSnmpOid) {
    int i = 0;
    try {
      i = (int)paramSnmpOid.getOidArc(0);
    } catch (SnmpStatusException snmpStatusException) {
      return null;
    } 
    return (i >= this.datas.length) ? null : this.datas[i];
  }
  
  public final SnmpOid getNext(SnmpOid paramSnmpOid) {
    int i = 0;
    if (paramSnmpOid == null && this.datas != null && this.datas.length >= 1)
      return new SnmpOid(0L); 
    try {
      i = (int)paramSnmpOid.getOidArc(0);
    } catch (SnmpStatusException snmpStatusException) {
      return null;
    } 
    return (i < this.datas.length - 1) ? new SnmpOid((i + 1)) : null;
  }
  
  public final boolean contains(SnmpOid paramSnmpOid) {
    int i = 0;
    try {
      i = (int)paramSnmpOid.getOidArc(0);
    } catch (SnmpStatusException snmpStatusException) {
      return false;
    } 
    return (i < this.datas.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snm\\util\SnmpLoadedClassData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */