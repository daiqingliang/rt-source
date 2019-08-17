package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpUnknownModelException;
import com.sun.jmx.snmp.internal.SnmpAccessControlModel;
import com.sun.jmx.snmp.internal.SnmpEngineImpl;
import java.util.logging.Level;

class AcmChecker {
  SnmpAccessControlModel model = null;
  
  String principal = null;
  
  int securityLevel = -1;
  
  int version = -1;
  
  int pduType = -1;
  
  int securityModel = -1;
  
  byte[] contextName = null;
  
  SnmpEngineImpl engine = null;
  
  LongList l = null;
  
  AcmChecker(SnmpMibRequest paramSnmpMibRequest) {
    this.engine = (SnmpEngineImpl)paramSnmpMibRequest.getEngine();
    if (this.engine != null && this.engine.isCheckOidActivated())
      try {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "AcmChecker(SnmpMibRequest)", "SNMP V3 Access Control to be done"); 
        this.model = (SnmpAccessControlModel)this.engine.getAccessControlSubSystem().getModel(3);
        this.principal = paramSnmpMibRequest.getPrincipal();
        this.securityLevel = paramSnmpMibRequest.getSecurityLevel();
        this.pduType = (paramSnmpMibRequest.getPdu()).type;
        this.version = paramSnmpMibRequest.getRequestPduVersion();
        this.securityModel = paramSnmpMibRequest.getSecurityModel();
        this.contextName = paramSnmpMibRequest.getAccessContextName();
        this.l = new LongList();
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          StringBuilder stringBuilder = (new StringBuilder()).append("Will check oid for : principal : ").append(this.principal).append("; securityLevel : ").append(this.securityLevel).append("; pduType : ").append(this.pduType).append("; version : ").append(this.version).append("; securityModel : ").append(this.securityModel).append("; contextName : ").append(this.contextName);
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "AcmChecker(SnmpMibRequest)", stringBuilder.toString());
        } 
      } catch (SnmpUnknownModelException snmpUnknownModelException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "AcmChecker(SnmpMibRequest)", "Unknown Model, no ACM check."); 
      }  
  }
  
  void add(int paramInt, long paramLong) {
    if (this.model != null)
      this.l.add(paramInt, paramLong); 
  }
  
  void remove(int paramInt) {
    if (this.model != null)
      this.l.remove(paramInt); 
  }
  
  void add(int paramInt1, long[] paramArrayOfLong, int paramInt2, int paramInt3) {
    if (this.model != null)
      this.l.add(paramInt1, paramArrayOfLong, paramInt2, paramInt3); 
  }
  
  void remove(int paramInt1, int paramInt2) {
    if (this.model != null)
      this.l.remove(paramInt1, paramInt2); 
  }
  
  void checkCurrentOid() throws SnmpStatusException {
    if (this.model != null) {
      SnmpOid snmpOid = new SnmpOid(this.l.toArray());
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "checkCurrentOid", "Checking access for : " + snmpOid); 
      this.model.checkAccess(this.version, this.principal, this.securityLevel, this.pduType, this.securityModel, this.contextName, snmpOid);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\AcmChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */