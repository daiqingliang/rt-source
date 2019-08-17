package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;

public abstract class SnmpMibEntry extends SnmpMibNode implements Serializable {
  public abstract boolean isVariable(long paramLong);
  
  public abstract boolean isReadable(long paramLong);
  
  public long getNextVarId(long paramLong, Object paramObject) throws SnmpStatusException {
    long l;
    for (l = super.getNextVarId(paramLong, paramObject); !isReadable(l); l = super.getNextVarId(l, paramObject));
    return l;
  }
  
  public void validateVarId(long paramLong, Object paramObject) throws SnmpStatusException {
    if (!isVariable(paramLong))
      throw new SnmpStatusException(2); 
  }
  
  public abstract void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException;
  
  public abstract void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException;
  
  public abstract void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpMibEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */