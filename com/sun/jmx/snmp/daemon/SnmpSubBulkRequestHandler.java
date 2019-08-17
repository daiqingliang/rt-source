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
import java.util.Enumeration;
import java.util.logging.Level;

class SnmpSubBulkRequestHandler extends SnmpSubRequestHandler {
  private SnmpAdaptorServer server = null;
  
  protected int nonRepeat = 0;
  
  protected int maxRepeat = 0;
  
  protected int globalR = 0;
  
  protected int size = 0;
  
  protected SnmpSubBulkRequestHandler(SnmpEngine paramSnmpEngine, SnmpAdaptorServer paramSnmpAdaptorServer, SnmpIncomingRequest paramSnmpIncomingRequest, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu, int paramInt1, int paramInt2, int paramInt3) {
    super(paramSnmpEngine, paramSnmpIncomingRequest, paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpAdaptorServer, paramSnmpPdu, paramInt1, paramInt2, paramInt3);
  }
  
  protected SnmpSubBulkRequestHandler(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpMibAgent paramSnmpMibAgent, SnmpPdu paramSnmpPdu, int paramInt1, int paramInt2, int paramInt3) {
    super(paramSnmpMibAgent, paramSnmpPdu);
    init(paramSnmpAdaptorServer, paramSnmpPdu, paramInt1, paramInt2, paramInt3);
  }
  
  public void run() {
    this.size = this.varBind.size();
    try {
      threadContext = ThreadContext.push("SnmpUserData", this.data);
      try {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:getBulk operation on " + this.agent.getMibName()); 
        this.agent.getBulk(createMibRequest(this.varBind, this.version, this.data), this.nonRepeat, this.maxRepeat);
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
  
  private void init(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpPdu paramSnmpPdu, int paramInt1, int paramInt2, int paramInt3) {
    this.server = paramSnmpAdaptorServer;
    this.nonRepeat = paramInt1;
    this.maxRepeat = paramInt2;
    this.globalR = paramInt3;
    int i = this.translation.length;
    SnmpVarBind[] arrayOfSnmpVarBind = paramSnmpPdu.varBindList;
    SnmpSubRequestHandler.NonSyncVector nonSyncVector = (SnmpSubRequestHandler.NonSyncVector)this.varBind;
    for (byte b = 0; b < i; b++) {
      this.translation[b] = b;
      SnmpVarBind snmpVarBind = new SnmpVarBind((arrayOfSnmpVarBind[b]).oid, (arrayOfSnmpVarBind[b]).value);
      nonSyncVector.addNonSyncElement(snmpVarBind);
    } 
  }
  
  private SnmpVarBind findVarBind(SnmpVarBind paramSnmpVarBind1, SnmpVarBind paramSnmpVarBind2) {
    if (paramSnmpVarBind1 == null)
      return null; 
    if (paramSnmpVarBind2.oid == null)
      return paramSnmpVarBind1; 
    if (paramSnmpVarBind1.value == SnmpVarBind.endOfMibView)
      return paramSnmpVarBind2; 
    if (paramSnmpVarBind2.value == SnmpVarBind.endOfMibView)
      return paramSnmpVarBind1; 
    SnmpValue snmpValue = paramSnmpVarBind2.value;
    int i = paramSnmpVarBind1.oid.compareTo(paramSnmpVarBind2.oid);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Comparing OID element : " + paramSnmpVarBind1.oid + " with result : " + paramSnmpVarBind2.oid);
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Values element : " + paramSnmpVarBind1.value + " result : " + paramSnmpVarBind2.value);
    } 
    if (i < 0)
      return paramSnmpVarBind1; 
    if (i == 0) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", " oid overlapping. Oid : " + paramSnmpVarBind1.oid + "value :" + paramSnmpVarBind1.value);
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Already present varBind : " + paramSnmpVarBind2);
      } 
      SnmpOid snmpOid = paramSnmpVarBind2.oid;
      SnmpMibAgent snmpMibAgent = this.server.getAgentMib(snmpOid);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Deeper agent : " + snmpMibAgent); 
      if (snmpMibAgent == this.agent) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "The current agent is the deeper one. Update the value with the current one"); 
        return paramSnmpVarBind1;
      } 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "The current agent is not the deeper one. return the previous one."); 
      return paramSnmpVarBind2;
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "The right varBind is the already present one"); 
    return paramSnmpVarBind2;
  }
  
  protected void updateResult(SnmpVarBind[] paramArrayOfSnmpVarBind) {
    Enumeration enumeration = this.varBind.elements();
    int i = paramArrayOfSnmpVarBind.length;
    int j;
    for (j = 0; j < this.size; j++) {
      if (!enumeration.hasMoreElements())
        return; 
      int k = this.translation[j];
      if (k >= i) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateResult", "Position '" + k + "' is out of bound..."); 
      } else {
        SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
        if (snmpVarBind != null) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Non repeaters Current element : " + snmpVarBind + " from agent : " + this.agent); 
          SnmpVarBind snmpVarBind1 = findVarBind(snmpVarBind, paramArrayOfSnmpVarBind[k]);
          if (snmpVarBind1 != null)
            paramArrayOfSnmpVarBind[k] = snmpVarBind1; 
        } 
      } 
    } 
    j = this.size - this.nonRepeat;
    for (byte b = 2; b <= this.maxRepeat; b++) {
      for (int k = 0; k < j; k++) {
        int m = (b - 1) * this.globalR + this.translation[this.nonRepeat + k];
        if (m >= i)
          return; 
        if (!enumeration.hasMoreElements())
          return; 
        SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
        if (snmpVarBind != null) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Repeaters Current element : " + snmpVarBind + " from agent : " + this.agent); 
          SnmpVarBind snmpVarBind1 = findVarBind(snmpVarBind, paramArrayOfSnmpVarBind[m]);
          if (snmpVarBind1 != null)
            paramArrayOfSnmpVarBind[m] = snmpVarBind1; 
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSubBulkRequestHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */