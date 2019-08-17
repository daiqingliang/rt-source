package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.EnumJvmClassesVerboseLevel;
import sun.management.snmp.jvmmib.JvmClassLoadingMBean;

public class JvmClassLoadingImpl implements JvmClassLoadingMBean {
  static final EnumJvmClassesVerboseLevel JvmClassesVerboseLevelVerbose = new EnumJvmClassesVerboseLevel("verbose");
  
  static final EnumJvmClassesVerboseLevel JvmClassesVerboseLevelSilent = new EnumJvmClassesVerboseLevel("silent");
  
  public JvmClassLoadingImpl(SnmpMib paramSnmpMib) {}
  
  public JvmClassLoadingImpl(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {}
  
  static ClassLoadingMXBean getClassLoadingMXBean() { return ManagementFactory.getClassLoadingMXBean(); }
  
  public EnumJvmClassesVerboseLevel getJvmClassesVerboseLevel() throws SnmpStatusException { return getClassLoadingMXBean().isVerbose() ? JvmClassesVerboseLevelVerbose : JvmClassesVerboseLevelSilent; }
  
  public void setJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel paramEnumJvmClassesVerboseLevel) throws SnmpStatusException {
    boolean bool;
    if (JvmClassesVerboseLevelVerbose.equals(paramEnumJvmClassesVerboseLevel)) {
      bool = true;
    } else if (JvmClassesVerboseLevelSilent.equals(paramEnumJvmClassesVerboseLevel)) {
      bool = false;
    } else {
      throw new SnmpStatusException(10);
    } 
    getClassLoadingMXBean().setVerbose(bool);
  }
  
  public void checkJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel paramEnumJvmClassesVerboseLevel) throws SnmpStatusException {
    if (JvmClassesVerboseLevelVerbose.equals(paramEnumJvmClassesVerboseLevel))
      return; 
    if (JvmClassesVerboseLevelSilent.equals(paramEnumJvmClassesVerboseLevel))
      return; 
    throw new SnmpStatusException(10);
  }
  
  public Long getJvmClassesUnloadedCount() throws SnmpStatusException { return new Long(getClassLoadingMXBean().getUnloadedClassCount()); }
  
  public Long getJvmClassesTotalLoadedCount() throws SnmpStatusException { return new Long(getClassLoadingMXBean().getTotalLoadedClassCount()); }
  
  public Long getJvmClassesLoadedCount() throws SnmpStatusException { return new Long(getClassLoadingMXBean().getLoadedClassCount()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmClassLoadingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */