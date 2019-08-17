package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public abstract class SnmpListTableCache extends SnmpTableCache {
  protected abstract SnmpOid getIndex(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2);
  
  protected Object getData(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2) { return paramObject2; }
  
  protected SnmpCachedData updateCachedDatas(Object paramObject, List<?> paramList) {
    boolean bool = (paramList == null) ? 0 : paramList.size();
    if (!bool)
      return null; 
    long l = System.currentTimeMillis();
    Iterator iterator = paramList.iterator();
    TreeMap treeMap = new TreeMap(SnmpCachedData.oidComparator);
    for (byte b = 0; iterator.hasNext(); b++) {
      Object object1 = iterator.next();
      SnmpOid snmpOid = getIndex(paramObject, paramList, b, object1);
      Object object2 = getData(paramObject, paramList, b, object1);
      if (snmpOid != null)
        treeMap.put(snmpOid, object2); 
    } 
    return new SnmpCachedData(l, treeMap);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snm\\util\SnmpListTableCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */