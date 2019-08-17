package sun.management.snmp.util;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class SnmpNamedListTableCache extends SnmpListTableCache {
  protected TreeMap<String, SnmpOid> names = new TreeMap();
  
  protected long last = 0L;
  
  boolean wrapped = false;
  
  static final MibLogger log = new MibLogger(SnmpNamedListTableCache.class);
  
  protected abstract String getKey(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2);
  
  protected SnmpOid makeIndex(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2) {
    if (++this.last > 4294967295L) {
      log.debug("makeIndex", "Index wrapping...");
      this.last = 0L;
      this.wrapped = true;
    } 
    if (!this.wrapped)
      return new SnmpOid(this.last); 
    for (byte b = 1; b < 4294967295L; b++) {
      if (++this.last > 4294967295L)
        this.last = 1L; 
      SnmpOid snmpOid = new SnmpOid(this.last);
      if (this.names == null)
        return snmpOid; 
      if (!this.names.containsValue(snmpOid)) {
        if (paramObject1 == null)
          return snmpOid; 
        if (!((Map)paramObject1).containsValue(snmpOid))
          return snmpOid; 
      } 
    } 
    return null;
  }
  
  protected SnmpOid getIndex(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2) {
    String str = getKey(paramObject1, paramList, paramInt, paramObject2);
    Object object = (this.names == null || str == null) ? null : this.names.get(str);
    SnmpOid snmpOid = (object != null) ? (SnmpOid)object : makeIndex(paramObject1, paramList, paramInt, paramObject2);
    if (paramObject1 != null && str != null && snmpOid != null) {
      Map map = (Map)Util.cast(paramObject1);
      map.put(str, snmpOid);
    } 
    log.debug("getIndex", "key=" + str + ", index=" + snmpOid);
    return snmpOid;
  }
  
  protected SnmpCachedData updateCachedDatas(Object paramObject, List<?> paramList) {
    TreeMap treeMap = new TreeMap();
    SnmpCachedData snmpCachedData = super.updateCachedDatas(paramObject, paramList);
    this.names = treeMap;
    return snmpCachedData;
  }
  
  protected abstract List<?> loadRawDatas(Map<Object, Object> paramMap);
  
  protected abstract String getRawDatasKey();
  
  protected List<?> getRawDatas(Map<Object, Object> paramMap, String paramString) {
    List list = null;
    if (paramMap != null)
      list = (List)paramMap.get(paramString); 
    if (list == null) {
      list = loadRawDatas(paramMap);
      if (list != null && paramMap != null)
        paramMap.put(paramString, list); 
    } 
    return list;
  }
  
  protected SnmpCachedData updateCachedDatas(Object paramObject) {
    Map map = (paramObject instanceof Map) ? (Map)Util.cast(paramObject) : null;
    List list = getRawDatas(map, getRawDatasKey());
    log.debug("updateCachedDatas", "rawDatas.size()=" + ((list == null) ? "<no data>" : ("" + list.size())));
    TreeMap treeMap = new TreeMap();
    SnmpCachedData snmpCachedData = super.updateCachedDatas(treeMap, list);
    this.names = treeMap;
    return snmpCachedData;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snm\\util\SnmpNamedListTableCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */