package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpInt;
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

public class JvmMemPoolEntryMeta extends SnmpMibEntry implements Serializable, SnmpStandardMetaServer {
  static final long serialVersionUID = 7220682779249102830L;
  
  protected JvmMemPoolEntryMBean node;
  
  protected SnmpStandardObjectServer objectserver = null;
  
  public JvmMemPoolEntryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) {
    this.objectserver = paramSnmpStandardObjectServer;
    this.varList = new int[20];
    this.varList[0] = 33;
    this.varList[1] = 32;
    this.varList[2] = 31;
    this.varList[3] = 133;
    this.varList[4] = 132;
    this.varList[5] = 131;
    this.varList[6] = 13;
    this.varList[7] = 12;
    this.varList[8] = 11;
    this.varList[9] = 10;
    this.varList[10] = 112;
    this.varList[11] = 111;
    this.varList[12] = 110;
    this.varList[13] = 5;
    this.varList[14] = 4;
    this.varList[15] = 3;
    this.varList[16] = 2;
    this.varList[17] = 23;
    this.varList[18] = 22;
    this.varList[19] = 21;
    SnmpMibNode.sort(this.varList);
  }
  
  public SnmpValue get(long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 33:
        return new SnmpCounter64(this.node.getJvmMemPoolCollectMaxSize());
      case 32:
        return new SnmpCounter64(this.node.getJvmMemPoolCollectCommitted());
      case 31:
        return new SnmpCounter64(this.node.getJvmMemPoolCollectUsed());
      case 133:
        return new SnmpInt(this.node.getJvmMemPoolCollectThreshdSupport());
      case 132:
        return new SnmpCounter64(this.node.getJvmMemPoolCollectThreshdCount());
      case 131:
        return new SnmpCounter64(this.node.getJvmMemPoolCollectThreshold());
      case 13:
        return new SnmpCounter64(this.node.getJvmMemPoolMaxSize());
      case 12:
        return new SnmpCounter64(this.node.getJvmMemPoolCommitted());
      case 11:
        return new SnmpCounter64(this.node.getJvmMemPoolUsed());
      case 10:
        return new SnmpCounter64(this.node.getJvmMemPoolInitSize());
      case 112:
        return new SnmpInt(this.node.getJvmMemPoolThreshdSupport());
      case 111:
        return new SnmpCounter64(this.node.getJvmMemPoolThreshdCount());
      case 110:
        return new SnmpCounter64(this.node.getJvmMemPoolThreshold());
      case 5:
        return new SnmpCounter64(this.node.getJvmMemPoolPeakReset());
      case 4:
        return new SnmpInt(this.node.getJvmMemPoolState());
      case 3:
        return new SnmpInt(this.node.getJvmMemPoolType());
      case 2:
        return new SnmpString(this.node.getJvmMemPoolName());
      case 23:
        return new SnmpCounter64(this.node.getJvmMemPoolPeakMaxSize());
      case 1:
        throw new SnmpStatusException(224);
      case 22:
        return new SnmpCounter64(this.node.getJvmMemPoolPeakCommitted());
      case 21:
        return new SnmpCounter64(this.node.getJvmMemPoolPeakUsed());
    } 
    throw new SnmpStatusException(225);
  }
  
  public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 33:
        throw new SnmpStatusException(17);
      case 32:
        throw new SnmpStatusException(17);
      case 31:
        throw new SnmpStatusException(17);
      case 133:
        throw new SnmpStatusException(17);
      case 132:
        throw new SnmpStatusException(17);
      case 131:
        if (paramSnmpValue instanceof SnmpCounter64) {
          this.node.setJvmMemPoolCollectThreshold(((SnmpCounter64)paramSnmpValue).toLong());
          return new SnmpCounter64(this.node.getJvmMemPoolCollectThreshold());
        } 
        throw new SnmpStatusException(7);
      case 13:
        throw new SnmpStatusException(17);
      case 12:
        throw new SnmpStatusException(17);
      case 11:
        throw new SnmpStatusException(17);
      case 10:
        throw new SnmpStatusException(17);
      case 112:
        throw new SnmpStatusException(17);
      case 111:
        throw new SnmpStatusException(17);
      case 110:
        if (paramSnmpValue instanceof SnmpCounter64) {
          this.node.setJvmMemPoolThreshold(((SnmpCounter64)paramSnmpValue).toLong());
          return new SnmpCounter64(this.node.getJvmMemPoolThreshold());
        } 
        throw new SnmpStatusException(7);
      case 5:
        if (paramSnmpValue instanceof SnmpCounter64) {
          this.node.setJvmMemPoolPeakReset(((SnmpCounter64)paramSnmpValue).toLong());
          return new SnmpCounter64(this.node.getJvmMemPoolPeakReset());
        } 
        throw new SnmpStatusException(7);
      case 4:
        throw new SnmpStatusException(17);
      case 3:
        throw new SnmpStatusException(17);
      case 2:
        throw new SnmpStatusException(17);
      case 23:
        throw new SnmpStatusException(17);
      case 1:
        throw new SnmpStatusException(17);
      case 22:
        throw new SnmpStatusException(17);
      case 21:
        throw new SnmpStatusException(17);
    } 
    throw new SnmpStatusException(17);
  }
  
  public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 33:
        throw new SnmpStatusException(17);
      case 32:
        throw new SnmpStatusException(17);
      case 31:
        throw new SnmpStatusException(17);
      case 133:
        throw new SnmpStatusException(17);
      case 132:
        throw new SnmpStatusException(17);
      case 131:
        if (paramSnmpValue instanceof SnmpCounter64) {
          this.node.checkJvmMemPoolCollectThreshold(((SnmpCounter64)paramSnmpValue).toLong());
        } else {
          throw new SnmpStatusException(7);
        } 
        return;
      case 13:
        throw new SnmpStatusException(17);
      case 12:
        throw new SnmpStatusException(17);
      case 11:
        throw new SnmpStatusException(17);
      case 10:
        throw new SnmpStatusException(17);
      case 112:
        throw new SnmpStatusException(17);
      case 111:
        throw new SnmpStatusException(17);
      case 110:
        if (paramSnmpValue instanceof SnmpCounter64) {
          this.node.checkJvmMemPoolThreshold(((SnmpCounter64)paramSnmpValue).toLong());
        } else {
          throw new SnmpStatusException(7);
        } 
        return;
      case 5:
        if (paramSnmpValue instanceof SnmpCounter64) {
          this.node.checkJvmMemPoolPeakReset(((SnmpCounter64)paramSnmpValue).toLong());
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
      case 23:
        throw new SnmpStatusException(17);
      case 1:
        throw new SnmpStatusException(17);
      case 22:
        throw new SnmpStatusException(17);
      case 21:
        throw new SnmpStatusException(17);
    } 
    throw new SnmpStatusException(17);
  }
  
  protected void setInstance(JvmMemPoolEntryMBean paramJvmMemPoolEntryMBean) { this.node = paramJvmMemPoolEntryMBean; }
  
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
      case 10:
      case 11:
      case 12:
      case 13:
      case 21:
      case 22:
      case 23:
      case 31:
      case 32:
      case 33:
      case 110:
      case 111:
      case 112:
      case 131:
      case 132:
      case 133:
        return true;
    } 
    return false;
  }
  
  public boolean isReadable(long paramLong) {
    switch ((int)paramLong) {
      case 2:
      case 3:
      case 4:
      case 5:
      case 10:
      case 11:
      case 12:
      case 13:
      case 21:
      case 22:
      case 23:
      case 31:
      case 32:
      case 33:
      case 110:
      case 111:
      case 112:
      case 131:
      case 132:
      case 133:
        return true;
    } 
    return false;
  }
  
  public boolean skipVariable(long paramLong, Object paramObject, int paramInt) {
    switch ((int)paramLong) {
      case 5:
      case 10:
      case 11:
      case 12:
      case 13:
      case 23:
      case 31:
      case 32:
      case 33:
      case 110:
      case 111:
      case 131:
      case 132:
        if (paramInt == 0)
          return true; 
        break;
      case 1:
        return true;
      case 21:
      case 22:
        if (paramInt == 0)
          return true; 
        break;
    } 
    return super.skipVariable(paramLong, paramObject, paramInt);
  }
  
  public String getAttributeName(long paramLong) throws SnmpStatusException {
    switch ((int)paramLong) {
      case 33:
        return "JvmMemPoolCollectMaxSize";
      case 32:
        return "JvmMemPoolCollectCommitted";
      case 31:
        return "JvmMemPoolCollectUsed";
      case 133:
        return "JvmMemPoolCollectThreshdSupport";
      case 132:
        return "JvmMemPoolCollectThreshdCount";
      case 131:
        return "JvmMemPoolCollectThreshold";
      case 13:
        return "JvmMemPoolMaxSize";
      case 12:
        return "JvmMemPoolCommitted";
      case 11:
        return "JvmMemPoolUsed";
      case 10:
        return "JvmMemPoolInitSize";
      case 112:
        return "JvmMemPoolThreshdSupport";
      case 111:
        return "JvmMemPoolThreshdCount";
      case 110:
        return "JvmMemPoolThreshold";
      case 5:
        return "JvmMemPoolPeakReset";
      case 4:
        return "JvmMemPoolState";
      case 3:
        return "JvmMemPoolType";
      case 2:
        return "JvmMemPoolName";
      case 23:
        return "JvmMemPoolPeakMaxSize";
      case 1:
        return "JvmMemPoolIndex";
      case 22:
        return "JvmMemPoolPeakCommitted";
      case 21:
        return "JvmMemPoolPeakUsed";
    } 
    throw new SnmpStatusException(225);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmMemPoolEntryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */