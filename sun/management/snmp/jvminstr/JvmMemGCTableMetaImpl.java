package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmMemGCTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmMemGCTableMetaImpl extends JvmMemGCTableMeta {
  static final long serialVersionUID = 8250461197108867607L;
  
  private JvmMemManagerTableMetaImpl managers = null;
  
  private static GCTableFilter filter = new GCTableFilter();
  
  static final MibLogger log = new MibLogger(JvmMemGCTableMetaImpl.class);
  
  public JvmMemGCTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) { super(paramSnmpMib, paramSnmpStandardObjectServer); }
  
  private final JvmMemManagerTableMetaImpl getManagers(SnmpMib paramSnmpMib) {
    if (this.managers == null)
      this.managers = (JvmMemManagerTableMetaImpl)paramSnmpMib.getRegisteredTableMeta("JvmMemManagerTable"); 
    return this.managers;
  }
  
  protected SnmpTableHandler getHandler(Object paramObject) {
    JvmMemManagerTableMetaImpl jvmMemManagerTableMetaImpl = getManagers(this.theMib);
    return jvmMemManagerTableMetaImpl.getHandler(paramObject);
  }
  
  protected SnmpOid getNextOid(Object paramObject) throws SnmpStatusException { return getNextOid(null, paramObject); }
  
  protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject) throws SnmpStatusException {
    boolean bool = log.isDebugOn();
    try {
      if (bool)
        log.debug("getNextOid", "previous=" + paramSnmpOid); 
      SnmpTableHandler snmpTableHandler = getHandler(paramObject);
      if (snmpTableHandler == null) {
        if (bool)
          log.debug("getNextOid", "handler is null!"); 
        throw new SnmpStatusException(224);
      } 
      SnmpOid snmpOid = filter.getNext(snmpTableHandler, paramSnmpOid);
      if (bool)
        log.debug("getNextOid", "next=" + snmpOid); 
      if (snmpOid == null)
        throw new SnmpStatusException(224); 
      return snmpOid;
    } catch (RuntimeException runtimeException) {
      if (bool)
        log.debug("getNextOid", runtimeException); 
      throw runtimeException;
    } 
  }
  
  protected boolean contains(SnmpOid paramSnmpOid, Object paramObject) {
    SnmpTableHandler snmpTableHandler = getHandler(paramObject);
    return (snmpTableHandler == null) ? false : filter.contains(snmpTableHandler, paramSnmpOid);
  }
  
  public Object getEntry(SnmpOid paramSnmpOid) throws SnmpStatusException {
    if (paramSnmpOid == null)
      throw new SnmpStatusException(224); 
    Map map = JvmContextFactory.getUserData();
    long l = paramSnmpOid.getOidArc(0);
    String str = (map == null) ? null : ("JvmMemGCTable.entry." + l);
    if (map != null) {
      Object object1 = map.get(str);
      if (object1 != null)
        return object1; 
    } 
    SnmpTableHandler snmpTableHandler = getHandler(map);
    if (snmpTableHandler == null)
      throw new SnmpStatusException(224); 
    Object object = filter.getData(snmpTableHandler, paramSnmpOid);
    if (object == null)
      throw new SnmpStatusException(224); 
    JvmMemGCEntryImpl jvmMemGCEntryImpl = new JvmMemGCEntryImpl((GarbageCollectorMXBean)object, (int)l);
    if (map != null && jvmMemGCEntryImpl != null)
      map.put(str, jvmMemGCEntryImpl); 
    return jvmMemGCEntryImpl;
  }
  
  protected static class GCTableFilter {
    public SnmpOid getNext(SnmpCachedData param1SnmpCachedData, SnmpOid param1SnmpOid) {
      byte b2;
      boolean bool = JvmMemGCTableMetaImpl.log.isDebugOn();
      byte b1 = (param1SnmpOid == null) ? -1 : param1SnmpCachedData.find(param1SnmpOid);
      if (bool)
        JvmMemGCTableMetaImpl.log.debug("GCTableFilter", "oid=" + param1SnmpOid + " at insertion=" + b1); 
      if (b1 > -1) {
        b2 = b1 + 1;
      } else {
        b2 = -b1 - 1;
      } 
      while (b2 < param1SnmpCachedData.indexes.length) {
        if (bool)
          JvmMemGCTableMetaImpl.log.debug("GCTableFilter", "next=" + b2); 
        Object object = param1SnmpCachedData.datas[b2];
        if (bool)
          JvmMemGCTableMetaImpl.log.debug("GCTableFilter", "value[" + b2 + "]=" + ((MemoryManagerMXBean)object).getName()); 
        if (object instanceof GarbageCollectorMXBean) {
          if (bool)
            JvmMemGCTableMetaImpl.log.debug("GCTableFilter", ((MemoryManagerMXBean)object).getName() + " is a  GarbageCollectorMXBean."); 
          return param1SnmpCachedData.indexes[b2];
        } 
        if (bool)
          JvmMemGCTableMetaImpl.log.debug("GCTableFilter", ((MemoryManagerMXBean)object).getName() + " is not a  GarbageCollectorMXBean: " + object.getClass().getName()); 
        b2++;
      } 
      return null;
    }
    
    public SnmpOid getNext(SnmpTableHandler param1SnmpTableHandler, SnmpOid param1SnmpOid) {
      if (param1SnmpTableHandler instanceof SnmpCachedData)
        return getNext((SnmpCachedData)param1SnmpTableHandler, param1SnmpOid); 
      SnmpOid snmpOid = param1SnmpOid;
      do {
        snmpOid = param1SnmpTableHandler.getNext(snmpOid);
        Object object = param1SnmpTableHandler.getData(snmpOid);
        if (object instanceof GarbageCollectorMXBean)
          return snmpOid; 
      } while (snmpOid != null);
      return null;
    }
    
    public Object getData(SnmpTableHandler param1SnmpTableHandler, SnmpOid param1SnmpOid) {
      Object object = param1SnmpTableHandler.getData(param1SnmpOid);
      return (object instanceof GarbageCollectorMXBean) ? object : null;
    }
    
    public boolean contains(SnmpTableHandler param1SnmpTableHandler, SnmpOid param1SnmpOid) { return (param1SnmpTableHandler.getData(param1SnmpOid) instanceof GarbageCollectorMXBean); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmMemGCTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */