package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.ThreadContext;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.internal.SnmpIncomingRequest;
import java.util.logging.Level;

class SnmpSubNextRequestHandler extends SnmpSubRequestHandler {
  private SnmpAdaptorServer server = null;
  
  protected SnmpSubNextRequestHandler(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu) {
    super(paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpPdu, paramSnmpAdaptorServer);
  }
  
  protected SnmpSubNextRequestHandler(SnmpEngine paramSnmpEngine, SnmpAdaptorServer paramSnmpAdaptorServer, SnmpIncomingRequest paramSnmpIncomingRequest, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu) {
    super(paramSnmpEngine, paramSnmpIncomingRequest, paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpPdu, paramSnmpAdaptorServer);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubNextRequestHandler.class.getName(), "SnmpSubNextRequestHandler", "Constructor : " + this); 
  }
  
  private void init(SnmpPdu paramSnmpPdu, SnmpAdaptorServer paramSnmpAdaptorServer) {
    this.server = paramSnmpAdaptorServer;
    int i = this.translation.length;
    SnmpVarBind[] arrayOfSnmpVarBind = paramSnmpPdu.varBindList;
    SnmpSubRequestHandler.NonSyncVector nonSyncVector = (SnmpSubRequestHandler.NonSyncVector)this.varBind;
    for (byte b = 0; b < i; b++) {
      this.translation[b] = b;
      SnmpVarBind snmpVarBind = new SnmpVarBind((arrayOfSnmpVarBind[b]).oid, (arrayOfSnmpVarBind[b]).value);
      nonSyncVector.addNonSyncElement(snmpVarBind);
    } 
  }
  
  public void run() {
    try {
      threadContext = ThreadContext.push("SnmpUserData", this.data);
      try {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:getNext operation on " + this.agent.getMibName()); 
        this.agent.getNext(createMibRequest(this.varBind, 1, this.data));
      } finally {
        ThreadContext.restore(threadContext);
      } 
    } catch (SnmpStatusException snmpStatusException) {
      this.errorStatus = snmpStatusException.getStatus();
      this.errorIndex = snmpStatusException.getErrorIndex();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:an Snmp error occurred during the operation", snmpStatusException); 
    } catch (Exception exception) {
      this.errorStatus = 5;
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:a generic error occurred during the operation", exception); 
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:operation completed"); 
  }
  
  protected void updateRequest(SnmpVarBind paramSnmpVarBind, int paramInt) {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateRequest", "Copy :" + paramSnmpVarBind); 
    int i = this.varBind.size();
    this.translation[i] = paramInt;
    SnmpVarBind snmpVarBind = new SnmpVarBind(paramSnmpVarBind.oid, paramSnmpVarBind.value);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateRequest", "Copied :" + snmpVarBind); 
    this.varBind.addElement(snmpVarBind);
  }
  
  protected void updateResult(SnmpVarBind[] paramArrayOfSnmpVarBind) {
    int i = this.varBind.size();
    for (byte b = 0; b < i; b++) {
      int j = this.translation[b];
      SnmpVarBind snmpVarBind1 = (SnmpVarBind)((SnmpSubRequestHandler.NonSyncVector)this.varBind).elementAtNonSync(b);
      SnmpVarBind snmpVarBind2 = paramArrayOfSnmpVarBind[j];
      if (snmpVarBind2 == null) {
        paramArrayOfSnmpVarBind[j] = snmpVarBind1;
      } else {
        SnmpValue snmpValue = snmpVarBind2.value;
        if (snmpValue == null || snmpValue == SnmpVarBind.endOfMibView) {
          if (snmpVarBind1 != null && snmpVarBind1.value != SnmpVarBind.endOfMibView)
            paramArrayOfSnmpVarBind[j] = snmpVarBind1; 
        } else if (snmpVarBind1 != null && snmpVarBind1.value != SnmpVarBind.endOfMibView) {
          int k = snmpVarBind1.oid.compareTo(snmpVarBind2.oid);
          if (k < 0) {
            paramArrayOfSnmpVarBind[j] = snmpVarBind1;
          } else if (k == 0) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", " oid overlapping. Oid : " + snmpVarBind1.oid + "value :" + snmpVarBind1.value);
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Already present varBind : " + snmpVarBind2);
            } 
            SnmpOid snmpOid = snmpVarBind2.oid;
            SnmpMibAgent snmpMibAgent = this.server.getAgentMib(snmpOid);
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Deeper agent : " + snmpMibAgent); 
            if (snmpMibAgent == this.agent) {
              if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
                JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "The current agent is the deeper one. Update the value with the current one"); 
              (paramArrayOfSnmpVarBind[j]).value = snmpVarBind1.value;
            } 
          } 
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSubNextRequestHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */