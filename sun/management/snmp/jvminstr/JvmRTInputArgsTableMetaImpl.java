package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmRTInputArgsTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmRTInputArgsTableMetaImpl extends JvmRTInputArgsTableMeta {
  static final long serialVersionUID = -2083438094888099238L;
  
  private SnmpTableCache cache = new JvmRTInputArgsTableCache(this, -1L);
  
  static final MibLogger log = new MibLogger(JvmRTInputArgsTableMetaImpl.class);
  
  public JvmRTInputArgsTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) { super(paramSnmpMib, paramSnmpStandardObjectServer); }
  
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
    String str = (map == null) ? null : ("JvmRTInputArgsTable.entry." + paramSnmpOid.toString());
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
    JvmRTInputArgsEntryImpl jvmRTInputArgsEntryImpl = new JvmRTInputArgsEntryImpl((String)object, (int)paramSnmpOid.getOidArc(0));
    if (map != null && jvmRTInputArgsEntryImpl != null)
      map.put(str, jvmRTInputArgsEntryImpl); 
    return jvmRTInputArgsEntryImpl;
  }
  
  protected SnmpTableHandler getHandler(Object paramObject) {
    Object object;
    if (paramObject instanceof Map) {
      object = (Map)Util.cast(paramObject);
    } else {
      object = null;
    } 
    if (object != null) {
      SnmpTableHandler snmpTableHandler1 = (SnmpTableHandler)object.get("JvmRTInputArgsTable.handler");
      if (snmpTableHandler1 != null)
        return snmpTableHandler1; 
    } 
    SnmpTableHandler snmpTableHandler = this.cache.getTableHandler();
    if (object != null && snmpTableHandler != null)
      object.put("JvmRTInputArgsTable.handler", snmpTableHandler); 
    return snmpTableHandler;
  }
  
  private static class JvmRTInputArgsTableCache extends SnmpTableCache {
    static final long serialVersionUID = 1693751105464785192L;
    
    private JvmRTInputArgsTableMetaImpl meta;
    
    JvmRTInputArgsTableCache(JvmRTInputArgsTableMetaImpl param1JvmRTInputArgsTableMetaImpl, long param1Long) {
      this.meta = param1JvmRTInputArgsTableMetaImpl;
      this.validity = param1Long;
    }
    
    public SnmpTableHandler getTableHandler() {
      Map map = JvmContextFactory.getUserData();
      return getTableDatas(map);
    }
    
    protected SnmpCachedData updateCachedDatas(Object param1Object) {
      String[] arrayOfString = JvmRuntimeImpl.getInputArguments(param1Object);
      long l = System.currentTimeMillis();
      SnmpOid[] arrayOfSnmpOid = new SnmpOid[arrayOfString.length];
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfSnmpOid[b] = new SnmpOid((b + true)); 
      return new SnmpCachedData(l, arrayOfSnmpOid, arrayOfString);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmRTInputArgsTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */