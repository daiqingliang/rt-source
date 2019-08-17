package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibEntry;
import com.sun.jmx.snmp.agent.SnmpMibNode;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;

public class JvmMemMgrPoolRelEntryMeta extends SnmpMibEntry implements Serializable, SnmpStandardMetaServer {
  static final long serialVersionUID = 7414270971113459798L;
  
  protected JvmMemMgrPoolRelEntryMBean node;
  
  protected SnmpStandardObjectServer objectserver = null;
  
  public JvmMemMgrPoolRelEntryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) {
    this.objectserver = paramSnmpStandardObjectServer;
    this.varList = new int[2];
    this.varList[0] = 3;
    this.varList[1] = 2;
    SnmpMibNode.sort(this.varList);
  }
  
  public SnmpValue get(long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 3:
        return new SnmpString(this.node.getJvmMemMgrRelPoolName());
      case 2:
        return new SnmpString(this.node.getJvmMemMgrRelManagerName());
    } 
    throw new SnmpStatusException(225);
  }
  
  public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 3:
        throw new SnmpStatusException(17);
      case 2:
        throw new SnmpStatusException(17);
    } 
    throw new SnmpStatusException(17);
  }
  
  public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 3:
        throw new SnmpStatusException(17);
      case 2:
        throw new SnmpStatusException(17);
    } 
    throw new SnmpStatusException(17);
  }
  
  protected void setInstance(JvmMemMgrPoolRelEntryMBean paramJvmMemMgrPoolRelEntryMBean) { this.node = paramJvmMemMgrPoolRelEntryMBean; }
  
  public void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException { this.objectserver.get(this, paramSnmpMibSubRequest, paramInt); }
  
  public void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException { this.objectserver.set(this, paramSnmpMibSubRequest, paramInt); }
  
  public void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException { this.objectserver.check(this, paramSnmpMibSubRequest, paramInt); }
  
  public boolean isVariable(long paramLong) {
    switch ((int)paramLong) {
      case 2:
      case 3:
        return true;
    } 
    return false;
  }
  
  public boolean isReadable(long paramLong) {
    switch ((int)paramLong) {
      case 2:
      case 3:
        return true;
    } 
    return false;
  }
  
  public boolean skipVariable(long paramLong, Object paramObject, int paramInt) { return super.skipVariable(paramLong, paramObject, paramInt); }
  
  public String getAttributeName(long paramLong) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 3:
        return "JvmMemMgrRelPoolName";
      case 2:
        return "JvmMemMgrRelManagerName";
    } 
    throw new SnmpStatusException(225);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmMemMgrPoolRelEntryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */