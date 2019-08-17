package com.sun.jmx.snmp;

public interface SnmpEngineFactory {
  SnmpEngine createEngine(SnmpEngineParameters paramSnmpEngineParameters);
  
  SnmpEngine createEngine(SnmpEngineParameters paramSnmpEngineParameters, InetAddressAcl paramInetAddressAcl);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpEngineFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */