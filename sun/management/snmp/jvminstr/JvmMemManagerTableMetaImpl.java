package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryManagerMXBean;
import java.util.List;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmMemManagerTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpNamedListTableCache;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmMemManagerTableMetaImpl extends JvmMemManagerTableMeta {
  static final long serialVersionUID = 36176771566817592L;
  
  protected SnmpTableCache cache;
  
  static final MibLogger log = new MibLogger(JvmMemManagerTableMetaImpl.class);
  
  public JvmMemManagerTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) {
    super(paramSnmpMib, paramSnmpStandardObjectServer);
    this.cache = new JvmMemManagerTableCache(((JVM_MANAGEMENT_MIB_IMPL)paramSnmpMib).validity());
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
    if (paramSnmpOid == null)
      throw new SnmpStatusException(224); 
    Map map = JvmContextFactory.getUserData();
    long l = paramSnmpOid.getOidArc(0);
    String str = (map == null) ? null : ("JvmMemManagerTable.entry." + l);
    if (map != null) {
      Object object1 = map.get(str);
      if (object1 != null)
        return object1; 
    } 
    SnmpTableHandler snmpTableHandler = getHandler(map);
    if (snmpTableHandler == null)
      throw new SnmpStatusException(224); 
    Object object = snmpTableHandler.getData(paramSnmpOid);
    if (object == null)
      throw new SnmpStatusException(224); 
    JvmMemManagerEntryImpl jvmMemManagerEntryImpl = new JvmMemManagerEntryImpl((MemoryManagerMXBean)object, (int)l);
    if (map != null && jvmMemManagerEntryImpl != null)
      map.put(str, jvmMemManagerEntryImpl); 
    return jvmMemManagerEntryImpl;
  }
  
  protected SnmpTableHandler getHandler(Object paramObject) {
    Object object;
    if (paramObject instanceof Map) {
      object = (Map)Util.cast(paramObject);
    } else {
      object = null;
    } 
    if (object != null) {
      SnmpTableHandler snmpTableHandler1 = (SnmpTableHandler)object.get("JvmMemManagerTable.handler");
      if (snmpTableHandler1 != null)
        return snmpTableHandler1; 
    } 
    SnmpTableHandler snmpTableHandler = this.cache.getTableHandler();
    if (object != null && snmpTableHandler != null)
      object.put("JvmMemManagerTable.handler", snmpTableHandler); 
    return snmpTableHandler;
  }
  
  private static class JvmMemManagerTableCache extends SnmpNamedListTableCache {
    static final long serialVersionUID = 6564294074653009240L;
    
    JvmMemManagerTableCache(long param1Long) { this.validity = param1Long; }
    
    protected String getKey(Object param1Object1, List<?> param1List, int param1Int, Object param1Object2) {
      if (param1Object2 == null)
        return null; 
      String str = ((MemoryManagerMXBean)param1Object2).getName();
      JvmMemManagerTableMetaImpl.log.debug("getKey", "key=" + str);
      return str;
    }
    
    public SnmpTableHandler getTableHandler() {
      Map map = JvmContextFactory.getUserData();
      return getTableDatas(map);
    }
    
    protected String getRawDatasKey() { return "JvmMemManagerTable.getMemoryManagers"; }
    
    protected List<MemoryManagerMXBean> loadRawDatas(Map<Object, Object> param1Map) { return ManagementFactory.getMemoryManagerMXBeans(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmMemManagerTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */