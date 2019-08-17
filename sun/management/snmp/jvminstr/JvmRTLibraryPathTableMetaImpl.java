package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmRTLibraryPathTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmRTLibraryPathTableMetaImpl extends JvmRTLibraryPathTableMeta {
  static final long serialVersionUID = 6713252710712502068L;
  
  private SnmpTableCache cache = new JvmRTLibraryPathTableCache(this, -1L);
  
  static final MibLogger log = new MibLogger(JvmRTLibraryPathTableMetaImpl.class);
  
  public JvmRTLibraryPathTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) { super(paramSnmpMib, paramSnmpStandardObjectServer); }
  
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
      log.debug("*** **** **** **** getNextOid", "next=" + snmpOid); 
    if (snmpOid == null)
      throw new SnmpStatusException(224); 
    return snmpOid;
  }
  
  protected boolean contains(SnmpOid paramSnmpOid, Object paramObject) {
    SnmpTableHandler snmpTableHandler = getHandler(paramObject);
    return (snmpTableHandler == null) ? false : snmpTableHandler.contains(paramSnmpOid);
  }
  
  public Object getEntry(SnmpOid paramSnmpOid) throws SnmpStatusException {
    boolean bool = log.isDebugOn();
    if (bool)
      log.debug("getEntry", "oid [" + paramSnmpOid + "]"); 
    if (paramSnmpOid == null || paramSnmpOid.getLength() != 1) {
      if (bool)
        log.debug("getEntry", "Invalid oid [" + paramSnmpOid + "]"); 
      throw new SnmpStatusException(224);
    } 
    Map map = JvmContextFactory.getUserData();
    String str = (map == null) ? null : ("JvmRTLibraryPathTable.entry." + paramSnmpOid.toString());
    if (map != null) {
      Object object1 = map.get(str);
      if (object1 != null) {
        if (bool)
          log.debug("getEntry", "Entry is already in the cache"); 
        return object1;
      } 
      if (bool)
        log.debug("getEntry", "Entry is not in the cache"); 
    } 
    SnmpTableHandler snmpTableHandler = getHandler(map);
    if (snmpTableHandler == null)
      throw new SnmpStatusException(224); 
    Object object = snmpTableHandler.getData(paramSnmpOid);
    if (object == null)
      throw new SnmpStatusException(224); 
    if (bool)
      log.debug("getEntry", "data is a: " + object.getClass().getName()); 
    JvmRTLibraryPathEntryImpl jvmRTLibraryPathEntryImpl = new JvmRTLibraryPathEntryImpl((String)object, (int)paramSnmpOid.getOidArc(0));
    if (map != null && jvmRTLibraryPathEntryImpl != null)
      map.put(str, jvmRTLibraryPathEntryImpl); 
    return jvmRTLibraryPathEntryImpl;
  }
  
  protected SnmpTableHandler getHandler(Object paramObject) {
    Object object;
    if (paramObject instanceof Map) {
      object = (Map)Util.cast(paramObject);
    } else {
      object = null;
    } 
    if (object != null) {
      SnmpTableHandler snmpTableHandler1 = (SnmpTableHandler)object.get("JvmRTLibraryPathTable.handler");
      if (snmpTableHandler1 != null)
        return snmpTableHandler1; 
    } 
    SnmpTableHandler snmpTableHandler = this.cache.getTableHandler();
    if (object != null && snmpTableHandler != null)
      object.put("JvmRTLibraryPathTable.handler", snmpTableHandler); 
    return snmpTableHandler;
  }
  
  private static class JvmRTLibraryPathTableCache extends SnmpTableCache {
    static final long serialVersionUID = 2035304445719393195L;
    
    private JvmRTLibraryPathTableMetaImpl meta;
    
    JvmRTLibraryPathTableCache(JvmRTLibraryPathTableMetaImpl param1JvmRTLibraryPathTableMetaImpl, long param1Long) {
      this.meta = param1JvmRTLibraryPathTableMetaImpl;
      this.validity = param1Long;
    }
    
    public SnmpTableHandler getTableHandler() {
      Map map = JvmContextFactory.getUserData();
      return getTableDatas(map);
    }
    
    protected SnmpCachedData updateCachedDatas(Object param1Object) {
      String[] arrayOfString = JvmRuntimeImpl.getLibraryPath(param1Object);
      long l = System.currentTimeMillis();
      int i = arrayOfString.length;
      SnmpOid[] arrayOfSnmpOid = new SnmpOid[i];
      for (byte b = 0; b < i; b++)
        arrayOfSnmpOid[b] = new SnmpOid((b + true)); 
      return new SnmpCachedData(l, arrayOfSnmpOid, arrayOfString);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmRTLibraryPathTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */