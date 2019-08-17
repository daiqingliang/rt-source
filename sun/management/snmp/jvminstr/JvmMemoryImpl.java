package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Map;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.EnumJvmMemoryGCCall;
import sun.management.snmp.jvmmib.EnumJvmMemoryGCVerboseLevel;
import sun.management.snmp.jvmmib.JvmMemoryMBean;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;

public class JvmMemoryImpl implements JvmMemoryMBean {
  static final EnumJvmMemoryGCCall JvmMemoryGCCallSupported = new EnumJvmMemoryGCCall("supported");
  
  static final EnumJvmMemoryGCCall JvmMemoryGCCallStart = new EnumJvmMemoryGCCall("start");
  
  static final EnumJvmMemoryGCCall JvmMemoryGCCallFailed = new EnumJvmMemoryGCCall("failed");
  
  static final EnumJvmMemoryGCCall JvmMemoryGCCallStarted = new EnumJvmMemoryGCCall("started");
  
  static final EnumJvmMemoryGCVerboseLevel JvmMemoryGCVerboseLevelVerbose = new EnumJvmMemoryGCVerboseLevel("verbose");
  
  static final EnumJvmMemoryGCVerboseLevel JvmMemoryGCVerboseLevelSilent = new EnumJvmMemoryGCVerboseLevel("silent");
  
  static final String heapMemoryTag = "jvmMemory.getHeapMemoryUsage";
  
  static final String nonHeapMemoryTag = "jvmMemory.getNonHeapMemoryUsage";
  
  static final Long Long0 = new Long(0L);
  
  static final MibLogger log = new MibLogger(JvmMemoryImpl.class);
  
  public JvmMemoryImpl(SnmpMib paramSnmpMib) {}
  
  public JvmMemoryImpl(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {}
  
  private MemoryUsage getMemoryUsage(MemoryType paramMemoryType) { return (paramMemoryType == MemoryType.HEAP) ? ManagementFactory.getMemoryMXBean().getHeapMemoryUsage() : ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage(); }
  
  MemoryUsage getNonHeapMemoryUsage() {
    try {
      Map map = JvmContextFactory.getUserData();
      if (map != null) {
        MemoryUsage memoryUsage1 = (MemoryUsage)map.get("jvmMemory.getNonHeapMemoryUsage");
        if (memoryUsage1 != null) {
          log.debug("getNonHeapMemoryUsage", "jvmMemory.getNonHeapMemoryUsage found in cache.");
          return memoryUsage1;
        } 
        MemoryUsage memoryUsage2 = getMemoryUsage(MemoryType.NON_HEAP);
        map.put("jvmMemory.getNonHeapMemoryUsage", memoryUsage2);
        return memoryUsage2;
      } 
      log.trace("getNonHeapMemoryUsage", "ERROR: should never come here!");
      return getMemoryUsage(MemoryType.NON_HEAP);
    } catch (RuntimeException runtimeException) {
      log.trace("getNonHeapMemoryUsage", "Failed to get NonHeapMemoryUsage: " + runtimeException);
      log.debug("getNonHeapMemoryUsage", runtimeException);
      throw runtimeException;
    } 
  }
  
  MemoryUsage getHeapMemoryUsage() {
    try {
      Map map = JvmContextFactory.getUserData();
      if (map != null) {
        MemoryUsage memoryUsage1 = (MemoryUsage)map.get("jvmMemory.getHeapMemoryUsage");
        if (memoryUsage1 != null) {
          log.debug("getHeapMemoryUsage", "jvmMemory.getHeapMemoryUsage found in cache.");
          return memoryUsage1;
        } 
        MemoryUsage memoryUsage2 = getMemoryUsage(MemoryType.HEAP);
        map.put("jvmMemory.getHeapMemoryUsage", memoryUsage2);
        return memoryUsage2;
      } 
      log.trace("getHeapMemoryUsage", "ERROR: should never come here!");
      return getMemoryUsage(MemoryType.HEAP);
    } catch (RuntimeException runtimeException) {
      log.trace("getHeapMemoryUsage", "Failed to get HeapMemoryUsage: " + runtimeException);
      log.debug("getHeapMemoryUsage", runtimeException);
      throw runtimeException;
    } 
  }
  
  public Long getJvmMemoryNonHeapMaxSize() throws SnmpStatusException {
    long l = getNonHeapMemoryUsage().getMax();
    return (l > -1L) ? new Long(l) : Long0;
  }
  
  public Long getJvmMemoryNonHeapCommitted() throws SnmpStatusException {
    long l = getNonHeapMemoryUsage().getCommitted();
    return (l > -1L) ? new Long(l) : Long0;
  }
  
  public Long getJvmMemoryNonHeapUsed() throws SnmpStatusException {
    long l = getNonHeapMemoryUsage().getUsed();
    return (l > -1L) ? new Long(l) : Long0;
  }
  
  public Long getJvmMemoryNonHeapInitSize() throws SnmpStatusException {
    long l = getNonHeapMemoryUsage().getInit();
    return (l > -1L) ? new Long(l) : Long0;
  }
  
  public Long getJvmMemoryHeapMaxSize() throws SnmpStatusException {
    long l = getHeapMemoryUsage().getMax();
    return (l > -1L) ? new Long(l) : Long0;
  }
  
  public EnumJvmMemoryGCCall getJvmMemoryGCCall() throws SnmpStatusException {
    Map map = JvmContextFactory.getUserData();
    if (map != null) {
      EnumJvmMemoryGCCall enumJvmMemoryGCCall = (EnumJvmMemoryGCCall)map.get("jvmMemory.getJvmMemoryGCCall");
      if (enumJvmMemoryGCCall != null)
        return enumJvmMemoryGCCall; 
    } 
    return JvmMemoryGCCallSupported;
  }
  
  public void setJvmMemoryGCCall(EnumJvmMemoryGCCall paramEnumJvmMemoryGCCall) throws SnmpStatusException {
    if (paramEnumJvmMemoryGCCall.intValue() == JvmMemoryGCCallStart.intValue()) {
      Map map = JvmContextFactory.getUserData();
      try {
        ManagementFactory.getMemoryMXBean().gc();
        if (map != null)
          map.put("jvmMemory.getJvmMemoryGCCall", JvmMemoryGCCallStarted); 
      } catch (Exception exception) {
        if (map != null)
          map.put("jvmMemory.getJvmMemoryGCCall", JvmMemoryGCCallFailed); 
      } 
      return;
    } 
    throw new SnmpStatusException(10);
  }
  
  public void checkJvmMemoryGCCall(EnumJvmMemoryGCCall paramEnumJvmMemoryGCCall) throws SnmpStatusException {
    if (paramEnumJvmMemoryGCCall.intValue() != JvmMemoryGCCallStart.intValue())
      throw new SnmpStatusException(10); 
  }
  
  public Long getJvmMemoryHeapCommitted() throws SnmpStatusException {
    long l = getHeapMemoryUsage().getCommitted();
    return (l > -1L) ? new Long(l) : Long0;
  }
  
  public EnumJvmMemoryGCVerboseLevel getJvmMemoryGCVerboseLevel() throws SnmpStatusException { return ManagementFactory.getMemoryMXBean().isVerbose() ? JvmMemoryGCVerboseLevelVerbose : JvmMemoryGCVerboseLevelSilent; }
  
  public void setJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel paramEnumJvmMemoryGCVerboseLevel) throws SnmpStatusException {
    if (JvmMemoryGCVerboseLevelVerbose.intValue() == paramEnumJvmMemoryGCVerboseLevel.intValue()) {
      ManagementFactory.getMemoryMXBean().setVerbose(true);
    } else {
      ManagementFactory.getMemoryMXBean().setVerbose(false);
    } 
  }
  
  public void checkJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel paramEnumJvmMemoryGCVerboseLevel) throws SnmpStatusException {}
  
  public Long getJvmMemoryHeapUsed() throws SnmpStatusException {
    long l = getHeapMemoryUsage().getUsed();
    return (l > -1L) ? new Long(l) : Long0;
  }
  
  public Long getJvmMemoryHeapInitSize() throws SnmpStatusException {
    long l = getHeapMemoryUsage().getInit();
    return (l > -1L) ? new Long(l) : Long0;
  }
  
  public Long getJvmMemoryPendingFinalCount() throws SnmpStatusException {
    long l = ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount();
    return (l > -1L) ? new Long((int)l) : new Long(0L);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmMemoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */