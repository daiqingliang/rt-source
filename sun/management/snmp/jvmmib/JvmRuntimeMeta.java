package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibGroup;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import javax.management.MBeanServer;

public class JvmRuntimeMeta extends SnmpMibGroup implements Serializable, SnmpStandardMetaServer {
  static final long serialVersionUID = 1994595220765880109L;
  
  protected JvmRuntimeMBean node;
  
  protected SnmpStandardObjectServer objectserver = null;
  
  protected JvmRTLibraryPathTableMeta tableJvmRTLibraryPathTable = null;
  
  protected JvmRTClassPathTableMeta tableJvmRTClassPathTable = null;
  
  protected JvmRTBootClassPathTableMeta tableJvmRTBootClassPathTable = null;
  
  protected JvmRTInputArgsTableMeta tableJvmRTInputArgsTable = null;
  
  public JvmRuntimeMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) {
    this.objectserver = paramSnmpStandardObjectServer;
    try {
      registerObject(23L);
      registerObject(22L);
      registerObject(21L);
      registerObject(9L);
      registerObject(20L);
      registerObject(8L);
      registerObject(7L);
      registerObject(6L);
      registerObject(5L);
      registerObject(4L);
      registerObject(3L);
      registerObject(12L);
      registerObject(11L);
      registerObject(2L);
      registerObject(1L);
      registerObject(10L);
    } catch (IllegalAccessException illegalAccessException) {
      throw new RuntimeException(illegalAccessException.getMessage());
    } 
  }
  
  public SnmpValue get(long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 23:
        throw new SnmpStatusException(224);
      case 22:
        throw new SnmpStatusException(224);
      case 21:
        throw new SnmpStatusException(224);
      case 9:
        return new SnmpInt(this.node.getJvmRTBootClassPathSupport());
      case 20:
        throw new SnmpStatusException(224);
      case 8:
        return new SnmpString(this.node.getJvmRTManagementSpecVersion());
      case 7:
        return new SnmpString(this.node.getJvmRTSpecVersion());
      case 6:
        return new SnmpString(this.node.getJvmRTSpecVendor());
      case 5:
        return new SnmpString(this.node.getJvmRTSpecName());
      case 4:
        return new SnmpString(this.node.getJvmRTVMVersion());
      case 3:
        return new SnmpString(this.node.getJvmRTVMVendor());
      case 12:
        return new SnmpCounter64(this.node.getJvmRTStartTimeMs());
      case 11:
        return new SnmpCounter64(this.node.getJvmRTUptimeMs());
      case 2:
        return new SnmpString(this.node.getJvmRTVMName());
      case 1:
        return new SnmpString(this.node.getJvmRTName());
      case 10:
        return new SnmpInt(this.node.getJvmRTInputArgsCount());
    } 
    throw new SnmpStatusException(225);
  }
  
  public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 23:
        throw new SnmpStatusException(17);
      case 22:
        throw new SnmpStatusException(17);
      case 21:
        throw new SnmpStatusException(17);
      case 9:
        throw new SnmpStatusException(17);
      case 20:
        throw new SnmpStatusException(17);
      case 8:
        throw new SnmpStatusException(17);
      case 7:
        throw new SnmpStatusException(17);
      case 6:
        throw new SnmpStatusException(17);
      case 5:
        throw new SnmpStatusException(17);
      case 4:
        throw new SnmpStatusException(17);
      case 3:
        throw new SnmpStatusException(17);
      case 12:
        throw new SnmpStatusException(17);
      case 11:
        throw new SnmpStatusException(17);
      case 2:
        throw new SnmpStatusException(17);
      case 1:
        throw new SnmpStatusException(17);
      case 10:
        throw new SnmpStatusException(17);
    } 
    throw new SnmpStatusException(17);
  }
  
  public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 23:
        throw new SnmpStatusException(17);
      case 22:
        throw new SnmpStatusException(17);
      case 21:
        throw new SnmpStatusException(17);
      case 9:
        throw new SnmpStatusException(17);
      case 20:
        throw new SnmpStatusException(17);
      case 8:
        throw new SnmpStatusException(17);
      case 7:
        throw new SnmpStatusException(17);
      case 6:
        throw new SnmpStatusException(17);
      case 5:
        throw new SnmpStatusException(17);
      case 4:
        throw new SnmpStatusException(17);
      case 3:
        throw new SnmpStatusException(17);
      case 12:
        throw new SnmpStatusException(17);
      case 11:
        throw new SnmpStatusException(17);
      case 2:
        throw new SnmpStatusException(17);
      case 1:
        throw new SnmpStatusException(17);
      case 10:
        throw new SnmpStatusException(17);
    } 
    throw new SnmpStatusException(17);
  }
  
  protected void setInstance(JvmRuntimeMBean paramJvmRuntimeMBean) { this.node = paramJvmRuntimeMBean; }
  
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
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
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
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
        return true;
    } 
    return false;
  }
  
  public boolean skipVariable(long paramLong, Object paramObject, int paramInt) {
    switch ((int)paramLong) {
      case 11:
      case 12:
        if (paramInt == 0)
          return true; 
        break;
    } 
    return super.skipVariable(paramLong, paramObject, paramInt);
  }
  
  public String getAttributeName(long paramLong) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 23:
        throw new SnmpStatusException(224);
      case 22:
        throw new SnmpStatusException(224);
      case 21:
        throw new SnmpStatusException(224);
      case 9:
        return "JvmRTBootClassPathSupport";
      case 20:
        throw new SnmpStatusException(224);
      case 8:
        return "JvmRTManagementSpecVersion";
      case 7:
        return "JvmRTSpecVersion";
      case 6:
        return "JvmRTSpecVendor";
      case 5:
        return "JvmRTSpecName";
      case 4:
        return "JvmRTVMVersion";
      case 3:
        return "JvmRTVMVendor";
      case 12:
        return "JvmRTStartTimeMs";
      case 11:
        return "JvmRTUptimeMs";
      case 2:
        return "JvmRTVMName";
      case 1:
        return "JvmRTName";
      case 10:
        return "JvmRTInputArgsCount";
    } 
    throw new SnmpStatusException(225);
  }
  
  public boolean isTable(long paramLong) {
    switch ((int)paramLong) {
      case 23:
        return true;
      case 22:
        return true;
      case 21:
        return true;
      case 20:
        return true;
    } 
    return false;
  }
  
  public SnmpMibTable getTable(long paramLong) {
    switch ((int)paramLong) {
      case 23:
        return this.tableJvmRTLibraryPathTable;
      case 22:
        return this.tableJvmRTClassPathTable;
      case 21:
        return this.tableJvmRTBootClassPathTable;
      case 20:
        return this.tableJvmRTInputArgsTable;
    } 
    return null;
  }
  
  public void registerTableNodes(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {
    this.tableJvmRTLibraryPathTable = createJvmRTLibraryPathTableMetaNode("JvmRTLibraryPathTable", "JvmRuntime", paramSnmpMib, paramMBeanServer);
    if (this.tableJvmRTLibraryPathTable != null) {
      this.tableJvmRTLibraryPathTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
      paramSnmpMib.registerTableMeta("JvmRTLibraryPathTable", this.tableJvmRTLibraryPathTable);
    } 
    this.tableJvmRTClassPathTable = createJvmRTClassPathTableMetaNode("JvmRTClassPathTable", "JvmRuntime", paramSnmpMib, paramMBeanServer);
    if (this.tableJvmRTClassPathTable != null) {
      this.tableJvmRTClassPathTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
      paramSnmpMib.registerTableMeta("JvmRTClassPathTable", this.tableJvmRTClassPathTable);
    } 
    this.tableJvmRTBootClassPathTable = createJvmRTBootClassPathTableMetaNode("JvmRTBootClassPathTable", "JvmRuntime", paramSnmpMib, paramMBeanServer);
    if (this.tableJvmRTBootClassPathTable != null) {
      this.tableJvmRTBootClassPathTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
      paramSnmpMib.registerTableMeta("JvmRTBootClassPathTable", this.tableJvmRTBootClassPathTable);
    } 
    this.tableJvmRTInputArgsTable = createJvmRTInputArgsTableMetaNode("JvmRTInputArgsTable", "JvmRuntime", paramSnmpMib, paramMBeanServer);
    if (this.tableJvmRTInputArgsTable != null) {
      this.tableJvmRTInputArgsTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
      paramSnmpMib.registerTableMeta("JvmRTInputArgsTable", this.tableJvmRTInputArgsTable);
    } 
  }
  
  protected JvmRTLibraryPathTableMeta createJvmRTLibraryPathTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmRTLibraryPathTableMeta(paramSnmpMib, this.objectserver); }
  
  protected JvmRTClassPathTableMeta createJvmRTClassPathTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmRTClassPathTableMeta(paramSnmpMib, this.objectserver); }
  
  protected JvmRTBootClassPathTableMeta createJvmRTBootClassPathTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmRTBootClassPathTableMeta(paramSnmpMib, this.objectserver); }
  
  protected JvmRTInputArgsTableMeta createJvmRTInputArgsTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmRTInputArgsTableMeta(paramSnmpMib, this.objectserver); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmRuntimeMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */