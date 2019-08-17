package com.sun.jmx.snmp;

public interface SnmpEngine {
  int getEngineTime();
  
  SnmpEngineId getEngineId();
  
  int getEngineBoots();
  
  SnmpUsmKeyHandler getUsmKeyHandler();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */