package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class JvmMemMgrPoolRelTableMeta extends SnmpMibTable implements Serializable {
  static final long serialVersionUID = -310733366542788998L;
  
  private JvmMemMgrPoolRelEntryMeta node;
  
  protected SnmpStandardObjectServer objectserver;
  
  public JvmMemMgrPoolRelTableMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) {
    super(paramSnmpMib);
    this.objectserver = paramSnmpStandardObjectServer;
  }
  
  protected JvmMemMgrPoolRelEntryMeta createJvmMemMgrPoolRelEntryMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmMemMgrPoolRelEntryMeta(paramSnmpMib, this.objectserver); }
  
  public void createNewEntry(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt) throws SnmpStatusException {
    if (this.factory != null) {
      this.factory.createNewEntry(paramSnmpMibSubRequest, paramSnmpOid, paramInt, this);
    } else {
      throw new SnmpStatusException(6);
    } 
  }
  
  public boolean isRegistrationRequired() { return false; }
  
  public void registerEntryNode(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { this.node = createJvmMemMgrPoolRelEntryMetaNode("JvmMemMgrPoolRelEntry", "JvmMemMgrPoolRelTable", paramSnmpMib, paramMBeanServer); }
  
  public void addEntry(SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject) throws SnmpStatusException {
    if (!(paramObject instanceof JvmMemMgrPoolRelEntryMBean))
      throw new ClassCastException("Entries for Table \"JvmMemMgrPoolRelTable\" must implement the \"JvmMemMgrPoolRelEntryMBean\" interface."); 
    super.addEntry(paramSnmpOid, paramObjectName, paramObject);
  }
  
  public void get(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt) throws SnmpStatusException {
    JvmMemMgrPoolRelEntryMBean jvmMemMgrPoolRelEntryMBean = (JvmMemMgrPoolRelEntryMBean)getEntry(paramSnmpOid);
    synchronized (this) {
      this.node.setInstance(jvmMemMgrPoolRelEntryMBean);
      this.node.get(paramSnmpMibSubRequest, paramInt);
    } 
  }
  
  public void set(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt) throws SnmpStatusException {
    if (paramSnmpMibSubRequest.getSize() == 0)
      return; 
    JvmMemMgrPoolRelEntryMBean jvmMemMgrPoolRelEntryMBean = (JvmMemMgrPoolRelEntryMBean)getEntry(paramSnmpOid);
    synchronized (this) {
      this.node.setInstance(jvmMemMgrPoolRelEntryMBean);
      this.node.set(paramSnmpMibSubRequest, paramInt);
    } 
  }
  
  public void check(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt) throws SnmpStatusException {
    if (paramSnmpMibSubRequest.getSize() == 0)
      return; 
    JvmMemMgrPoolRelEntryMBean jvmMemMgrPoolRelEntryMBean = (JvmMemMgrPoolRelEntryMBean)getEntry(paramSnmpOid);
    synchronized (this) {
      this.node.setInstance(jvmMemMgrPoolRelEntryMBean);
      this.node.check(paramSnmpMibSubRequest, paramInt);
    } 
  }
  
  public void validateVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject) throws SnmpStatusException { this.node.validateVarId(paramLong, paramObject); }
  
  public boolean isReadableEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject) throws SnmpStatusException { return this.node.isReadable(paramLong); }
  
  public long getNextVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject) throws SnmpStatusException {
    long l;
    for (l = this.node.getNextVarId(paramLong, paramObject); !isReadableEntryId(paramSnmpOid, l, paramObject); l = this.node.getNextVarId(l, paramObject));
    return l;
  }
  
  public boolean skipEntryVariable(SnmpOid paramSnmpOid, long paramLong, Object paramObject, int paramInt) {
    try {
      JvmMemMgrPoolRelEntryMBean jvmMemMgrPoolRelEntryMBean = (JvmMemMgrPoolRelEntryMBean)getEntry(paramSnmpOid);
      synchronized (this) {
        this.node.setInstance(jvmMemMgrPoolRelEntryMBean);
        return this.node.skipVariable(paramLong, paramObject, paramInt);
      } 
    } catch (SnmpStatusException snmpStatusException) {
      return false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmMemMgrPoolRelTableMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */