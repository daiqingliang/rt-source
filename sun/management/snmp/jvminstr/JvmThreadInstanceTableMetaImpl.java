package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.ThreadInfo;
import java.util.Map;
import java.util.TreeMap;
import sun.management.snmp.jvmmib.JvmThreadInstanceTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmThreadInstanceTableMetaImpl extends JvmThreadInstanceTableMeta {
  static final long serialVersionUID = -8432271929226397492L;
  
  public static final int MAX_STACK_TRACE_DEPTH = 0;
  
  protected SnmpTableCache cache;
  
  static final MibLogger log = new MibLogger(JvmThreadInstanceTableMetaImpl.class);
  
  static SnmpOid makeOid(long paramLong) {
    long[] arrayOfLong = new long[8];
    arrayOfLong[0] = paramLong >> 56 & 0xFFL;
    arrayOfLong[1] = paramLong >> 48 & 0xFFL;
    arrayOfLong[2] = paramLong >> 40 & 0xFFL;
    arrayOfLong[3] = paramLong >> 32 & 0xFFL;
    arrayOfLong[4] = paramLong >> 24 & 0xFFL;
    arrayOfLong[5] = paramLong >> 16 & 0xFFL;
    arrayOfLong[6] = paramLong >> 8 & 0xFFL;
    arrayOfLong[7] = paramLong & 0xFFL;
    return new SnmpOid(arrayOfLong);
  }
  
  static long makeId(SnmpOid paramSnmpOid) {
    null = 0L;
    long[] arrayOfLong = paramSnmpOid.longValue(false);
    null |= arrayOfLong[0] << 56;
    null |= arrayOfLong[1] << 48;
    null |= arrayOfLong[2] << 40;
    null |= arrayOfLong[3] << 32;
    null |= arrayOfLong[4] << 24;
    null |= arrayOfLong[5] << 16;
    null |= arrayOfLong[6] << 8;
    return arrayOfLong[7];
  }
  
  public JvmThreadInstanceTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) {
    super(paramSnmpMib, paramSnmpStandardObjectServer);
    this.cache = new JvmThreadInstanceTableCache(this, ((JVM_MANAGEMENT_MIB_IMPL)paramSnmpMib).validity());
    log.debug("JvmThreadInstanceTableMetaImpl", "Create Thread meta");
  }
  
  protected SnmpOid getNextOid(Object paramObject) throws SnmpStatusException {
    log.debug("JvmThreadInstanceTableMetaImpl", "getNextOid");
    return getNextOid(null, paramObject);
  }
  
  protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject) throws SnmpStatusException {
    log.debug("getNextOid", "previous=" + paramSnmpOid);
    SnmpTableHandler snmpTableHandler = getHandler(paramObject);
    if (snmpTableHandler == null) {
      log.debug("getNextOid", "handler is null!");
      throw new SnmpStatusException(224);
    } 
    SnmpOid snmpOid = paramSnmpOid;
    do {
      snmpOid = snmpTableHandler.getNext(snmpOid);
    } while (snmpOid != null && getJvmThreadInstance(paramObject, snmpOid) == null);
    log.debug("*** **** **** **** getNextOid", "next=" + snmpOid);
    if (snmpOid == null)
      throw new SnmpStatusException(224); 
    return snmpOid;
  }
  
  protected boolean contains(SnmpOid paramSnmpOid, Object paramObject) {
    SnmpTableHandler snmpTableHandler = getHandler(paramObject);
    if (snmpTableHandler == null)
      return false; 
    if (!snmpTableHandler.contains(paramSnmpOid))
      return false; 
    JvmThreadInstanceEntryImpl jvmThreadInstanceEntryImpl = getJvmThreadInstance(paramObject, paramSnmpOid);
    return (jvmThreadInstanceEntryImpl != null);
  }
  
  public Object getEntry(SnmpOid paramSnmpOid) throws SnmpStatusException {
    log.debug("*** **** **** **** getEntry", "oid [" + paramSnmpOid + "]");
    if (paramSnmpOid == null || paramSnmpOid.getLength() != 8) {
      log.debug("getEntry", "Invalid oid [" + paramSnmpOid + "]");
      throw new SnmpStatusException(224);
    } 
    Map map = JvmContextFactory.getUserData();
    SnmpTableHandler snmpTableHandler = getHandler(map);
    if (snmpTableHandler == null || !snmpTableHandler.contains(paramSnmpOid))
      throw new SnmpStatusException(224); 
    JvmThreadInstanceEntryImpl jvmThreadInstanceEntryImpl = getJvmThreadInstance(map, paramSnmpOid);
    if (jvmThreadInstanceEntryImpl == null)
      throw new SnmpStatusException(224); 
    return jvmThreadInstanceEntryImpl;
  }
  
  protected SnmpTableHandler getHandler(Object paramObject) {
    Object object;
    if (paramObject instanceof Map) {
      object = (Map)Util.cast(paramObject);
    } else {
      object = null;
    } 
    if (object != null) {
      SnmpTableHandler snmpTableHandler1 = (SnmpTableHandler)object.get("JvmThreadInstanceTable.handler");
      if (snmpTableHandler1 != null)
        return snmpTableHandler1; 
    } 
    SnmpTableHandler snmpTableHandler = this.cache.getTableHandler();
    if (object != null && snmpTableHandler != null)
      object.put("JvmThreadInstanceTable.handler", snmpTableHandler); 
    return snmpTableHandler;
  }
  
  private ThreadInfo getThreadInfo(long paramLong) { return JvmThreadingImpl.getThreadMXBean().getThreadInfo(paramLong, 0); }
  
  private ThreadInfo getThreadInfo(SnmpOid paramSnmpOid) { return getThreadInfo(makeId(paramSnmpOid)); }
  
  private JvmThreadInstanceEntryImpl getJvmThreadInstance(Object paramObject, SnmpOid paramSnmpOid) {
    JvmThreadInstanceEntryImpl jvmThreadInstanceEntryImpl = null;
    String str = null;
    Map map = null;
    boolean bool = log.isDebugOn();
    if (paramObject instanceof Map) {
      map = (Map)Util.cast(paramObject);
      str = "JvmThreadInstanceTable.entry." + paramSnmpOid.toString();
      jvmThreadInstanceEntryImpl = (JvmThreadInstanceEntryImpl)map.get(str);
    } 
    if (jvmThreadInstanceEntryImpl != null) {
      if (bool)
        log.debug("*** getJvmThreadInstance", "Entry found in cache: " + str); 
      return jvmThreadInstanceEntryImpl;
    } 
    if (bool)
      log.debug("*** getJvmThreadInstance", "Entry [" + paramSnmpOid + "] is not in cache"); 
    ThreadInfo threadInfo = null;
    try {
      threadInfo = getThreadInfo(paramSnmpOid);
    } catch (RuntimeException runtimeException) {
      log.trace("*** getJvmThreadInstance", "Failed to get thread info for rowOid: " + paramSnmpOid);
      log.debug("*** getJvmThreadInstance", runtimeException);
    } 
    if (threadInfo == null) {
      if (bool)
        log.debug("*** getJvmThreadInstance", "No entry by that oid [" + paramSnmpOid + "]"); 
      return null;
    } 
    jvmThreadInstanceEntryImpl = new JvmThreadInstanceEntryImpl(threadInfo, paramSnmpOid.toByte());
    if (map != null)
      map.put(str, jvmThreadInstanceEntryImpl); 
    if (bool)
      log.debug("*** getJvmThreadInstance", "Entry created for Thread OID [" + paramSnmpOid + "]"); 
    return jvmThreadInstanceEntryImpl;
  }
  
  private static class JvmThreadInstanceTableCache extends SnmpTableCache {
    static final long serialVersionUID = 4947330124563406878L;
    
    private final JvmThreadInstanceTableMetaImpl meta;
    
    JvmThreadInstanceTableCache(JvmThreadInstanceTableMetaImpl param1JvmThreadInstanceTableMetaImpl, long param1Long) {
      this.validity = param1Long;
      this.meta = param1JvmThreadInstanceTableMetaImpl;
    }
    
    public SnmpTableHandler getTableHandler() {
      Map map = JvmContextFactory.getUserData();
      return getTableDatas(map);
    }
    
    protected SnmpCachedData updateCachedDatas(Object param1Object) {
      long[] arrayOfLong = JvmThreadingImpl.getThreadMXBean().getAllThreadIds();
      long l = System.currentTimeMillis();
      SnmpOid[] arrayOfSnmpOid = new SnmpOid[arrayOfLong.length];
      TreeMap treeMap = new TreeMap(SnmpCachedData.oidComparator);
      for (byte b = 0; b < arrayOfLong.length; b++) {
        JvmThreadInstanceTableMetaImpl.log.debug("", "Making index for thread id [" + arrayOfLong[b] + "]");
        SnmpOid snmpOid = JvmThreadInstanceTableMetaImpl.makeOid(arrayOfLong[b]);
        treeMap.put(snmpOid, snmpOid);
      } 
      return new SnmpCachedData(l, treeMap);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmThreadInstanceTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */