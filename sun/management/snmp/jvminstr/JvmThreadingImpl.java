package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.EnumJvmThreadContentionMonitoring;
import sun.management.snmp.jvmmib.EnumJvmThreadCpuTimeMonitoring;
import sun.management.snmp.jvmmib.JvmThreadingMBean;
import sun.management.snmp.util.MibLogger;

public class JvmThreadingImpl implements JvmThreadingMBean {
  static final EnumJvmThreadCpuTimeMonitoring JvmThreadCpuTimeMonitoringUnsupported = new EnumJvmThreadCpuTimeMonitoring("unsupported");
  
  static final EnumJvmThreadCpuTimeMonitoring JvmThreadCpuTimeMonitoringEnabled = new EnumJvmThreadCpuTimeMonitoring("enabled");
  
  static final EnumJvmThreadCpuTimeMonitoring JvmThreadCpuTimeMonitoringDisabled = new EnumJvmThreadCpuTimeMonitoring("disabled");
  
  static final EnumJvmThreadContentionMonitoring JvmThreadContentionMonitoringUnsupported = new EnumJvmThreadContentionMonitoring("unsupported");
  
  static final EnumJvmThreadContentionMonitoring JvmThreadContentionMonitoringEnabled = new EnumJvmThreadContentionMonitoring("enabled");
  
  static final EnumJvmThreadContentionMonitoring JvmThreadContentionMonitoringDisabled = new EnumJvmThreadContentionMonitoring("disabled");
  
  private long jvmThreadPeakCountReset = 0L;
  
  static final MibLogger log = new MibLogger(JvmThreadingImpl.class);
  
  public JvmThreadingImpl(SnmpMib paramSnmpMib) { log.debug("JvmThreadingImpl", "Constructor"); }
  
  public JvmThreadingImpl(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { log.debug("JvmThreadingImpl", "Constructor with server"); }
  
  static ThreadMXBean getThreadMXBean() { return ManagementFactory.getThreadMXBean(); }
  
  public EnumJvmThreadCpuTimeMonitoring getJvmThreadCpuTimeMonitoring() throws SnmpStatusException {
    ThreadMXBean threadMXBean = getThreadMXBean();
    if (!threadMXBean.isThreadCpuTimeSupported()) {
      log.debug("getJvmThreadCpuTimeMonitoring", "Unsupported ThreadCpuTimeMonitoring");
      return JvmThreadCpuTimeMonitoringUnsupported;
    } 
    try {
      if (threadMXBean.isThreadCpuTimeEnabled()) {
        log.debug("getJvmThreadCpuTimeMonitoring", "Enabled ThreadCpuTimeMonitoring");
        return JvmThreadCpuTimeMonitoringEnabled;
      } 
      log.debug("getJvmThreadCpuTimeMonitoring", "Disabled ThreadCpuTimeMonitoring");
      return JvmThreadCpuTimeMonitoringDisabled;
    } catch (UnsupportedOperationException unsupportedOperationException) {
      log.debug("getJvmThreadCpuTimeMonitoring", "Newly unsupported ThreadCpuTimeMonitoring");
      return JvmThreadCpuTimeMonitoringUnsupported;
    } 
  }
  
  public void setJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring paramEnumJvmThreadCpuTimeMonitoring) throws SnmpStatusException {
    ThreadMXBean threadMXBean = getThreadMXBean();
    if (JvmThreadCpuTimeMonitoringEnabled.intValue() == paramEnumJvmThreadCpuTimeMonitoring.intValue()) {
      threadMXBean.setThreadCpuTimeEnabled(true);
    } else {
      threadMXBean.setThreadCpuTimeEnabled(false);
    } 
  }
  
  public void checkJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring paramEnumJvmThreadCpuTimeMonitoring) throws SnmpStatusException {
    if (JvmThreadCpuTimeMonitoringUnsupported.intValue() == paramEnumJvmThreadCpuTimeMonitoring.intValue()) {
      log.debug("checkJvmThreadCpuTimeMonitoring", "Try to set to illegal unsupported value");
      throw new SnmpStatusException(10);
    } 
    if (JvmThreadCpuTimeMonitoringEnabled.intValue() == paramEnumJvmThreadCpuTimeMonitoring.intValue() || JvmThreadCpuTimeMonitoringDisabled.intValue() == paramEnumJvmThreadCpuTimeMonitoring.intValue()) {
      ThreadMXBean threadMXBean = getThreadMXBean();
      if (threadMXBean.isThreadCpuTimeSupported())
        return; 
      log.debug("checkJvmThreadCpuTimeMonitoring", "Unsupported operation, can't set state");
      throw new SnmpStatusException(12);
    } 
    log.debug("checkJvmThreadCpuTimeMonitoring", "unknown enum value ");
    throw new SnmpStatusException(10);
  }
  
  public EnumJvmThreadContentionMonitoring getJvmThreadContentionMonitoring() throws SnmpStatusException {
    ThreadMXBean threadMXBean = getThreadMXBean();
    if (!threadMXBean.isThreadContentionMonitoringSupported()) {
      log.debug("getJvmThreadContentionMonitoring", "Unsupported ThreadContentionMonitoring");
      return JvmThreadContentionMonitoringUnsupported;
    } 
    if (threadMXBean.isThreadContentionMonitoringEnabled()) {
      log.debug("getJvmThreadContentionMonitoring", "Enabled ThreadContentionMonitoring");
      return JvmThreadContentionMonitoringEnabled;
    } 
    log.debug("getJvmThreadContentionMonitoring", "Disabled ThreadContentionMonitoring");
    return JvmThreadContentionMonitoringDisabled;
  }
  
  public void setJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring paramEnumJvmThreadContentionMonitoring) throws SnmpStatusException {
    ThreadMXBean threadMXBean = getThreadMXBean();
    if (JvmThreadContentionMonitoringEnabled.intValue() == paramEnumJvmThreadContentionMonitoring.intValue()) {
      threadMXBean.setThreadContentionMonitoringEnabled(true);
    } else {
      threadMXBean.setThreadContentionMonitoringEnabled(false);
    } 
  }
  
  public void checkJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring paramEnumJvmThreadContentionMonitoring) throws SnmpStatusException {
    if (JvmThreadContentionMonitoringUnsupported.intValue() == paramEnumJvmThreadContentionMonitoring.intValue()) {
      log.debug("checkJvmThreadContentionMonitoring", "Try to set to illegal unsupported value");
      throw new SnmpStatusException(10);
    } 
    if (JvmThreadContentionMonitoringEnabled.intValue() == paramEnumJvmThreadContentionMonitoring.intValue() || JvmThreadContentionMonitoringDisabled.intValue() == paramEnumJvmThreadContentionMonitoring.intValue()) {
      ThreadMXBean threadMXBean = getThreadMXBean();
      if (threadMXBean.isThreadContentionMonitoringSupported())
        return; 
      log.debug("checkJvmThreadContentionMonitoring", "Unsupported operation, can't set state");
      throw new SnmpStatusException(12);
    } 
    log.debug("checkJvmThreadContentionMonitoring", "Try to set to unknown value");
    throw new SnmpStatusException(10);
  }
  
  public Long getJvmThreadTotalStartedCount() throws SnmpStatusException { return new Long(getThreadMXBean().getTotalStartedThreadCount()); }
  
  public Long getJvmThreadPeakCount() throws SnmpStatusException { return new Long(getThreadMXBean().getPeakThreadCount()); }
  
  public Long getJvmThreadDaemonCount() throws SnmpStatusException { return new Long(getThreadMXBean().getDaemonThreadCount()); }
  
  public Long getJvmThreadCount() throws SnmpStatusException { return new Long(getThreadMXBean().getThreadCount()); }
  
  public Long getJvmThreadPeakCountReset() throws SnmpStatusException { return new Long(this.jvmThreadPeakCountReset); }
  
  public void setJvmThreadPeakCountReset(Long paramLong) throws SnmpStatusException {
    long l = paramLong.longValue();
    if (l > this.jvmThreadPeakCountReset) {
      long l1 = System.currentTimeMillis();
      getThreadMXBean().resetPeakThreadCount();
      this.jvmThreadPeakCountReset = l1;
      log.debug("setJvmThreadPeakCountReset", "jvmThreadPeakCountReset=" + l1);
    } 
  }
  
  public void checkJvmThreadPeakCountReset(Long paramLong) throws SnmpStatusException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmThreadingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */