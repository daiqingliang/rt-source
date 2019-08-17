package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter;
import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpGauge;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibGroup;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import javax.management.MBeanServer;

public class JvmThreadingMeta extends SnmpMibGroup implements Serializable, SnmpStandardMetaServer {
  static final long serialVersionUID = 5223833578005322854L;
  
  protected JvmThreadingMBean node;
  
  protected SnmpStandardObjectServer objectserver = null;
  
  protected JvmThreadInstanceTableMeta tableJvmThreadInstanceTable = null;
  
  public JvmThreadingMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) {
    this.objectserver = paramSnmpStandardObjectServer;
    try {
      registerObject(6L);
      registerObject(5L);
      registerObject(4L);
      registerObject(3L);
      registerObject(2L);
      registerObject(1L);
      registerObject(10L);
      registerObject(7L);
    } catch (IllegalAccessException illegalAccessException) {
      throw new RuntimeException(illegalAccessException.getMessage());
    } 
  }
  
  public SnmpValue get(long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 6:
        return new SnmpInt(this.node.getJvmThreadCpuTimeMonitoring());
      case 5:
        return new SnmpInt(this.node.getJvmThreadContentionMonitoring());
      case 4:
        return new SnmpCounter64(this.node.getJvmThreadTotalStartedCount());
      case 3:
        return new SnmpCounter(this.node.getJvmThreadPeakCount());
      case 2:
        return new SnmpGauge(this.node.getJvmThreadDaemonCount());
      case 1:
        return new SnmpGauge(this.node.getJvmThreadCount());
      case 10:
        throw new SnmpStatusException(224);
      case 7:
        return new SnmpCounter64(this.node.getJvmThreadPeakCountReset());
    } 
    throw new SnmpStatusException(225);
  }
  
  public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 6:
        if (paramSnmpValue instanceof SnmpInt) {
          try {
            this.node.setJvmThreadCpuTimeMonitoring(new EnumJvmThreadCpuTimeMonitoring(((SnmpInt)paramSnmpValue).toInteger()));
          } catch (IllegalArgumentException illegalArgumentException) {
            throw new SnmpStatusException(10);
          } 
          return new SnmpInt(this.node.getJvmThreadCpuTimeMonitoring());
        } 
        throw new SnmpStatusException(7);
      case 5:
        if (paramSnmpValue instanceof SnmpInt) {
          try {
            this.node.setJvmThreadContentionMonitoring(new EnumJvmThreadContentionMonitoring(((SnmpInt)paramSnmpValue).toInteger()));
          } catch (IllegalArgumentException illegalArgumentException) {
            throw new SnmpStatusException(10);
          } 
          return new SnmpInt(this.node.getJvmThreadContentionMonitoring());
        } 
        throw new SnmpStatusException(7);
      case 4:
        throw new SnmpStatusException(17);
      case 3:
        throw new SnmpStatusException(17);
      case 2:
        throw new SnmpStatusException(17);
      case 1:
        throw new SnmpStatusException(17);
      case 10:
        throw new SnmpStatusException(17);
      case 7:
        if (paramSnmpValue instanceof SnmpCounter64) {
          this.node.setJvmThreadPeakCountReset(((SnmpCounter64)paramSnmpValue).toLong());
          return new SnmpCounter64(this.node.getJvmThreadPeakCountReset());
        } 
        throw new SnmpStatusException(7);
    } 
    throw new SnmpStatusException(17);
  }
  
  public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 6:
        if (paramSnmpValue instanceof SnmpInt) {
          try {
            this.node.checkJvmThreadCpuTimeMonitoring(new EnumJvmThreadCpuTimeMonitoring(((SnmpInt)paramSnmpValue).toInteger()));
          } catch (IllegalArgumentException illegalArgumentException) {
            throw new SnmpStatusException(10);
          } 
        } else {
          throw new SnmpStatusException(7);
        } 
        return;
      case 5:
        if (paramSnmpValue instanceof SnmpInt) {
          try {
            this.node.checkJvmThreadContentionMonitoring(new EnumJvmThreadContentionMonitoring(((SnmpInt)paramSnmpValue).toInteger()));
          } catch (IllegalArgumentException illegalArgumentException) {
            throw new SnmpStatusException(10);
          } 
        } else {
          throw new SnmpStatusException(7);
        } 
        return;
      case 4:
        throw new SnmpStatusException(17);
      case 3:
        throw new SnmpStatusException(17);
      case 2:
        throw new SnmpStatusException(17);
      case 1:
        throw new SnmpStatusException(17);
      case 10:
        throw new SnmpStatusException(17);
      case 7:
        if (paramSnmpValue instanceof SnmpCounter64) {
          this.node.checkJvmThreadPeakCountReset(((SnmpCounter64)paramSnmpValue).toLong());
        } else {
          throw new SnmpStatusException(7);
        } 
        return;
    } 
    throw new SnmpStatusException(17);
  }
  
  protected void setInstance(JvmThreadingMBean paramJvmThreadingMBean) { this.node = paramJvmThreadingMBean; }
  
  public void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException { this.objectserver.get(this, paramSnmpMibSubRequest, paramInt); }
  
  public void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException { this.objectserver.set(this, paramSnmpMibSubRequest, paramInt); }
  
  public void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException { this.objectserver.check(this, paramSnmpMibSubRequest, paramInt); }
  
  public boolean isVariable(long paramLong) {
    switch ((int)paramLong) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
        return true;
    } 
    return false;
  }
  
  public boolean isReadable(long paramLong) {
    switch ((int)paramLong) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
        return true;
    } 
    return false;
  }
  
  public boolean skipVariable(long paramLong, Object paramObject, int paramInt) {
    switch ((int)paramLong) {
      case 4:
      case 7:
        if (paramInt == 0)
          return true; 
        break;
    } 
    return super.skipVariable(paramLong, paramObject, paramInt);
  }
  
  public String getAttributeName(long paramLong) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 6:
        return "JvmThreadCpuTimeMonitoring";
      case 5:
        return "JvmThreadContentionMonitoring";
      case 4:
        return "JvmThreadTotalStartedCount";
      case 3:
        return "JvmThreadPeakCount";
      case 2:
        return "JvmThreadDaemonCount";
      case 1:
        return "JvmThreadCount";
      case 10:
        throw new SnmpStatusException(224);
      case 7:
        return "JvmThreadPeakCountReset";
    } 
    throw new SnmpStatusException(225);
  }
  
  public boolean isTable(long paramLong) {
    switch ((int)paramLong) {
      case 10:
        return true;
    } 
    return false;
  }
  
  public SnmpMibTable getTable(long paramLong) {
    switch ((int)paramLong) {
      case 10:
        return this.tableJvmThreadInstanceTable;
    } 
    return null;
  }
  
  public void registerTableNodes(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {
    this.tableJvmThreadInstanceTable = createJvmThreadInstanceTableMetaNode("JvmThreadInstanceTable", "JvmThreading", paramSnmpMib, paramMBeanServer);
    if (this.tableJvmThreadInstanceTable != null) {
      this.tableJvmThreadInstanceTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
      paramSnmpMib.registerTableMeta("JvmThreadInstanceTable", this.tableJvmThreadInstanceTable);
    } 
  }
  
  protected JvmThreadInstanceTableMeta createJvmThreadInstanceTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmThreadInstanceTableMeta(paramSnmpMib, this.objectserver); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmThreadingMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */