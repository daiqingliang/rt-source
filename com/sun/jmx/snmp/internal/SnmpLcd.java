package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpEngineId;
import com.sun.jmx.snmp.SnmpUnknownModelLcdException;
import com.sun.jmx.snmp.SnmpUnknownSubSystemException;
import java.util.Hashtable;

public abstract class SnmpLcd {
  private Hashtable<SnmpSubSystem, SubSysLcdManager> subs = new Hashtable();
  
  public abstract int getEngineBoots();
  
  public abstract String getEngineId();
  
  public abstract void storeEngineBoots(int paramInt);
  
  public abstract void storeEngineId(SnmpEngineId paramSnmpEngineId);
  
  public void addModelLcd(SnmpSubSystem paramSnmpSubSystem, int paramInt, SnmpModelLcd paramSnmpModelLcd) {
    SubSysLcdManager subSysLcdManager = (SubSysLcdManager)this.subs.get(paramSnmpSubSystem);
    if (subSysLcdManager == null) {
      subSysLcdManager = new SubSysLcdManager();
      this.subs.put(paramSnmpSubSystem, subSysLcdManager);
    } 
    subSysLcdManager.addModelLcd(paramInt, paramSnmpModelLcd);
  }
  
  public void removeModelLcd(SnmpSubSystem paramSnmpSubSystem, int paramInt) throws SnmpUnknownModelLcdException, SnmpUnknownSubSystemException {
    SubSysLcdManager subSysLcdManager = (SubSysLcdManager)this.subs.get(paramSnmpSubSystem);
    if (subSysLcdManager != null) {
      SnmpModelLcd snmpModelLcd = subSysLcdManager.removeModelLcd(paramInt);
      if (snmpModelLcd == null)
        throw new SnmpUnknownModelLcdException("Model : " + paramInt); 
    } else {
      throw new SnmpUnknownSubSystemException(paramSnmpSubSystem.toString());
    } 
  }
  
  public SnmpModelLcd getModelLcd(SnmpSubSystem paramSnmpSubSystem, int paramInt) {
    SubSysLcdManager subSysLcdManager = (SubSysLcdManager)this.subs.get(paramSnmpSubSystem);
    return (subSysLcdManager == null) ? null : subSysLcdManager.getModelLcd(paramInt);
  }
  
  class SubSysLcdManager {
    private Hashtable<Integer, SnmpModelLcd> models = new Hashtable();
    
    public void addModelLcd(int param1Int, SnmpModelLcd param1SnmpModelLcd) { this.models.put(new Integer(param1Int), param1SnmpModelLcd); }
    
    public SnmpModelLcd getModelLcd(int param1Int) { return (SnmpModelLcd)this.models.get(new Integer(param1Int)); }
    
    public SnmpModelLcd removeModelLcd(int param1Int) { return (SnmpModelLcd)this.models.remove(new Integer(param1Int)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\internal\SnmpLcd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */