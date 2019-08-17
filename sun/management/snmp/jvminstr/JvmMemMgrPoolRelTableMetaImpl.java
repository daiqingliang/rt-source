package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import sun.management.snmp.jvmmib.JvmMemMgrPoolRelTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmMemMgrPoolRelTableMetaImpl extends JvmMemMgrPoolRelTableMeta implements Serializable {
  static final long serialVersionUID = 1896509775012355443L;
  
  protected SnmpTableCache cache;
  
  private JvmMemManagerTableMetaImpl managers = null;
  
  private JvmMemPoolTableMetaImpl pools = null;
  
  static final MibLogger log = new MibLogger(JvmMemMgrPoolRelTableMetaImpl.class);
  
  public JvmMemMgrPoolRelTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) {
    super(paramSnmpMib, paramSnmpStandardObjectServer);
    this.cache = new JvmMemMgrPoolRelTableCache(this, ((JVM_MANAGEMENT_MIB_IMPL)paramSnmpMib).validity());
  }
  
  private final JvmMemManagerTableMetaImpl getManagers(SnmpMib paramSnmpMib) {
    if (this.managers == null)
      this.managers = (JvmMemManagerTableMetaImpl)paramSnmpMib.getRegisteredTableMeta("JvmMemManagerTable"); 
    return this.managers;
  }
  
  private final JvmMemPoolTableMetaImpl getPools(SnmpMib paramSnmpMib) {
    if (this.pools == null)
      this.pools = (JvmMemPoolTableMetaImpl)paramSnmpMib.getRegisteredTableMeta("JvmMemPoolTable"); 
    return this.pools;
  }
  
  protected SnmpTableHandler getManagerHandler(Object paramObject) {
    JvmMemManagerTableMetaImpl jvmMemManagerTableMetaImpl = getManagers(this.theMib);
    return jvmMemManagerTableMetaImpl.getHandler(paramObject);
  }
  
  protected SnmpTableHandler getPoolHandler(Object paramObject) {
    JvmMemPoolTableMetaImpl jvmMemPoolTableMetaImpl = getPools(this.theMib);
    return jvmMemPoolTableMetaImpl.getHandler(paramObject);
  }
  
  protected SnmpOid getNextOid(Object paramObject) throws SnmpStatusException { return getNextOid(null, paramObject); }
  
  protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject) throws SnmpStatusException {
    boolean bool = log.isDebugOn();
    if (bool)
      log.debug("getNextOid", "previous=" + paramSnmpOid); 
    SnmpTableHandler snmpTableHandler = getHandler(paramObject);
    if (snmpTableHandler == null) {
      if (bool)
        log.debug("getNextOid", "handler is null!"); 
      throw new SnmpStatusException(224);
    } 
    SnmpOid snmpOid = snmpTableHandler.getNext(paramSnmpOid);
    if (bool)
      log.debug("getNextOid", "next=" + snmpOid); 
    if (snmpOid == null)
      throw new SnmpStatusException(224); 
    return snmpOid;
  }
  
  protected boolean contains(SnmpOid paramSnmpOid, Object paramObject) {
    SnmpTableHandler snmpTableHandler = getHandler(paramObject);
    return (snmpTableHandler == null) ? false : snmpTableHandler.contains(paramSnmpOid);
  }
  
  public Object getEntry(SnmpOid paramSnmpOid) throws SnmpStatusException {
    if (paramSnmpOid == null || paramSnmpOid.getLength() < 2)
      throw new SnmpStatusException(224); 
    Map map = JvmContextFactory.getUserData();
    long l1 = paramSnmpOid.getOidArc(0);
    long l2 = paramSnmpOid.getOidArc(1);
    String str = (map == null) ? null : ("JvmMemMgrPoolRelTable.entry." + l1 + "." + l2);
    if (map != null) {
      Object object1 = map.get(str);
      if (object1 != null)
        return object1; 
    } 
    SnmpTableHandler snmpTableHandler = getHandler(map);
    if (snmpTableHandler == null)
      throw new SnmpStatusException(224); 
    Object object = snmpTableHandler.getData(paramSnmpOid);
    if (!(object instanceof JvmMemMgrPoolRelEntryImpl))
      throw new SnmpStatusException(224); 
    JvmMemMgrPoolRelEntryImpl jvmMemMgrPoolRelEntryImpl = (JvmMemMgrPoolRelEntryImpl)object;
    if (map != null && jvmMemMgrPoolRelEntryImpl != null)
      map.put(str, jvmMemMgrPoolRelEntryImpl); 
    return jvmMemMgrPoolRelEntryImpl;
  }
  
  protected SnmpTableHandler getHandler(Object paramObject) {
    Object object;
    if (paramObject instanceof Map) {
      object = (Map)Util.cast(paramObject);
    } else {
      object = null;
    } 
    if (object != null) {
      SnmpTableHandler snmpTableHandler1 = (SnmpTableHandler)object.get("JvmMemMgrPoolRelTable.handler");
      if (snmpTableHandler1 != null)
        return snmpTableHandler1; 
    } 
    SnmpTableHandler snmpTableHandler = this.cache.getTableHandler();
    if (object != null && snmpTableHandler != null)
      object.put("JvmMemMgrPoolRelTable.handler", snmpTableHandler); 
    return snmpTableHandler;
  }
  
  private static class JvmMemMgrPoolRelTableCache extends SnmpTableCache {
    static final long serialVersionUID = 6059937161990659184L;
    
    private final JvmMemMgrPoolRelTableMetaImpl meta;
    
    JvmMemMgrPoolRelTableCache(JvmMemMgrPoolRelTableMetaImpl param1JvmMemMgrPoolRelTableMetaImpl, long param1Long) {
      this.validity = param1Long;
      this.meta = param1JvmMemMgrPoolRelTableMetaImpl;
    }
    
    public SnmpTableHandler getTableHandler() {
      Map map = JvmContextFactory.getUserData();
      return getTableDatas(map);
    }
    
    private static Map<String, SnmpOid> buildPoolIndexMap(SnmpTableHandler param1SnmpTableHandler) {
      if (param1SnmpTableHandler instanceof SnmpCachedData)
        return buildPoolIndexMap((SnmpCachedData)param1SnmpTableHandler); 
      HashMap hashMap = new HashMap();
      SnmpOid snmpOid = null;
      while ((snmpOid = param1SnmpTableHandler.getNext(snmpOid)) != null) {
        MemoryPoolMXBean memoryPoolMXBean = (MemoryPoolMXBean)param1SnmpTableHandler.getData(snmpOid);
        if (memoryPoolMXBean == null)
          continue; 
        String str = memoryPoolMXBean.getName();
        if (str == null)
          continue; 
        hashMap.put(str, snmpOid);
      } 
      return hashMap;
    }
    
    private static Map<String, SnmpOid> buildPoolIndexMap(SnmpCachedData param1SnmpCachedData) {
      if (param1SnmpCachedData == null)
        return Collections.emptyMap(); 
      SnmpOid[] arrayOfSnmpOid = param1SnmpCachedData.indexes;
      Object[] arrayOfObject = param1SnmpCachedData.datas;
      int i = arrayOfSnmpOid.length;
      HashMap hashMap = new HashMap(i);
      for (byte b = 0; b < i; b++) {
        SnmpOid snmpOid = arrayOfSnmpOid[b];
        if (snmpOid != null) {
          MemoryPoolMXBean memoryPoolMXBean = (MemoryPoolMXBean)arrayOfObject[b];
          if (memoryPoolMXBean != null) {
            String str = memoryPoolMXBean.getName();
            if (str != null)
              hashMap.put(str, snmpOid); 
          } 
        } 
      } 
      return hashMap;
    }
    
    protected SnmpCachedData updateCachedDatas(Object param1Object) {
      SnmpTableHandler snmpTableHandler1 = this.meta.getManagerHandler(param1Object);
      SnmpTableHandler snmpTableHandler2 = this.meta.getPoolHandler(param1Object);
      long l = System.currentTimeMillis();
      Map map = buildPoolIndexMap(snmpTableHandler2);
      TreeMap treeMap = new TreeMap(SnmpCachedData.oidComparator);
      updateTreeMap(treeMap, param1Object, snmpTableHandler1, snmpTableHandler2, map);
      return new SnmpCachedData(l, treeMap);
    }
    
    protected String[] getMemoryPools(Object param1Object, MemoryManagerMXBean param1MemoryManagerMXBean, long param1Long) {
      String str = "JvmMemManager." + param1Long + ".getMemoryPools";
      String[] arrayOfString = null;
      if (param1Object instanceof Map) {
        arrayOfString = (String[])((Map)param1Object).get(str);
        if (arrayOfString != null)
          return arrayOfString; 
      } 
      if (param1MemoryManagerMXBean != null)
        arrayOfString = param1MemoryManagerMXBean.getMemoryPoolNames(); 
      if (arrayOfString != null && param1Object instanceof Map) {
        Map map = (Map)Util.cast(param1Object);
        map.put(str, arrayOfString);
      } 
      return arrayOfString;
    }
    
    protected void updateTreeMap(TreeMap<SnmpOid, Object> param1TreeMap, Object param1Object, MemoryManagerMXBean param1MemoryManagerMXBean, SnmpOid param1SnmpOid, Map<String, SnmpOid> param1Map) {
      long l;
      try {
        l = param1SnmpOid.getOidArc(0);
      } catch (SnmpStatusException snmpStatusException) {
        JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", "Bad MemoryManager OID index: " + param1SnmpOid);
        JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", snmpStatusException);
        return;
      } 
      String[] arrayOfString = getMemoryPools(param1Object, param1MemoryManagerMXBean, l);
      if (arrayOfString == null || arrayOfString.length < 1)
        return; 
      String str = param1MemoryManagerMXBean.getName();
      for (byte b = 0; b < arrayOfString.length; b++) {
        String str1 = arrayOfString[b];
        if (str1 != null) {
          SnmpOid snmpOid = (SnmpOid)param1Map.get(str1);
          if (snmpOid != null) {
            long l1;
            try {
              l1 = snmpOid.getOidArc(0);
            } catch (SnmpStatusException snmpStatusException) {
              JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", "Bad MemoryPool OID index: " + snmpOid);
              JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", snmpStatusException);
            } 
            long[] arrayOfLong = { l, l1 };
            SnmpOid snmpOid1 = new SnmpOid(arrayOfLong);
            param1TreeMap.put(snmpOid1, new JvmMemMgrPoolRelEntryImpl(str, str1, (int)l, (int)l1));
          } 
        } 
      } 
    }
    
    protected void updateTreeMap(TreeMap<SnmpOid, Object> param1TreeMap, Object param1Object, SnmpTableHandler param1SnmpTableHandler1, SnmpTableHandler param1SnmpTableHandler2, Map<String, SnmpOid> param1Map) {
      if (param1SnmpTableHandler1 instanceof SnmpCachedData) {
        updateTreeMap(param1TreeMap, param1Object, (SnmpCachedData)param1SnmpTableHandler1, param1SnmpTableHandler2, param1Map);
        return;
      } 
      SnmpOid snmpOid = null;
      while ((snmpOid = param1SnmpTableHandler1.getNext(snmpOid)) != null) {
        MemoryManagerMXBean memoryManagerMXBean = (MemoryManagerMXBean)param1SnmpTableHandler1.getData(snmpOid);
        if (memoryManagerMXBean == null)
          continue; 
        updateTreeMap(param1TreeMap, param1Object, memoryManagerMXBean, snmpOid, param1Map);
      } 
    }
    
    protected void updateTreeMap(TreeMap<SnmpOid, Object> param1TreeMap, Object param1Object, SnmpCachedData param1SnmpCachedData, SnmpTableHandler param1SnmpTableHandler, Map<String, SnmpOid> param1Map) {
      SnmpOid[] arrayOfSnmpOid = param1SnmpCachedData.indexes;
      Object[] arrayOfObject = param1SnmpCachedData.datas;
      int i = arrayOfSnmpOid.length;
      for (int j = i - 1; j > -1; j--) {
        MemoryManagerMXBean memoryManagerMXBean = (MemoryManagerMXBean)arrayOfObject[j];
        if (memoryManagerMXBean != null)
          updateTreeMap(param1TreeMap, param1Object, memoryManagerMXBean, arrayOfSnmpOid[j], param1Map); 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmMemMgrPoolRelTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */