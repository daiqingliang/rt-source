package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public abstract class SnmpMib extends SnmpMibAgent implements Serializable {
  protected SnmpMibOid root = new SnmpMibOid();
  
  private long[] rootOid = null;
  
  protected String getGroupOid(String paramString1, String paramString2) { return paramString2; }
  
  protected ObjectName getGroupObjectName(String paramString1, String paramString2, String paramString3) throws MalformedObjectNameException { return new ObjectName(paramString3); }
  
  protected void registerGroupNode(String paramString1, String paramString2, ObjectName paramObjectName, SnmpMibNode paramSnmpMibNode, Object paramObject, MBeanServer paramMBeanServer) throws NotCompliantMBeanException, MBeanRegistrationException, InstanceAlreadyExistsException, IllegalAccessException {
    this.root.registerNode(paramString2, paramSnmpMibNode);
    if (paramMBeanServer != null && paramObjectName != null && paramObject != null)
      paramMBeanServer.registerMBean(paramObject, paramObjectName); 
  }
  
  public abstract void registerTableMeta(String paramString, SnmpMibTable paramSnmpMibTable);
  
  public abstract SnmpMibTable getRegisteredTableMeta(String paramString);
  
  public void get(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException {
    SnmpRequestTree snmpRequestTree = getHandlers(paramSnmpMibRequest, false, false, 160);
    SnmpRequestTree.Handler handler = null;
    SnmpMibNode snmpMibNode = null;
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "get", "Processing handlers for GET... "); 
    Enumeration enumeration = snmpRequestTree.getHandlers();
    while (enumeration.hasMoreElements()) {
      handler = (SnmpRequestTree.Handler)enumeration.nextElement();
      snmpMibNode = snmpRequestTree.getMetaNode(handler);
      int i = snmpRequestTree.getOidDepth(handler);
      Enumeration enumeration1 = snmpRequestTree.getSubRequests(handler);
      while (enumeration1.hasMoreElements())
        snmpMibNode.get((SnmpMibSubRequest)enumeration1.nextElement(), i); 
    } 
  }
  
  public void set(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException {
    SnmpRequestTree snmpRequestTree = null;
    if (paramSnmpMibRequest instanceof SnmpMibRequestImpl)
      snmpRequestTree = ((SnmpMibRequestImpl)paramSnmpMibRequest).getRequestTree(); 
    if (snmpRequestTree == null)
      snmpRequestTree = getHandlers(paramSnmpMibRequest, false, true, 163); 
    snmpRequestTree.switchCreationFlag(false);
    snmpRequestTree.setPduType(163);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "set", "Processing handlers for SET... "); 
    Enumeration enumeration = snmpRequestTree.getHandlers();
    while (enumeration.hasMoreElements()) {
      SnmpRequestTree.Handler handler = (SnmpRequestTree.Handler)enumeration.nextElement();
      SnmpMibNode snmpMibNode = snmpRequestTree.getMetaNode(handler);
      int i = snmpRequestTree.getOidDepth(handler);
      Enumeration enumeration1 = snmpRequestTree.getSubRequests(handler);
      while (enumeration1.hasMoreElements())
        snmpMibNode.set((SnmpMibSubRequest)enumeration1.nextElement(), i); 
    } 
  }
  
  public void check(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException {
    SnmpRequestTree snmpRequestTree = getHandlers(paramSnmpMibRequest, true, true, 253);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "check", "Processing handlers for CHECK... "); 
    Enumeration enumeration = snmpRequestTree.getHandlers();
    while (enumeration.hasMoreElements()) {
      SnmpRequestTree.Handler handler = (SnmpRequestTree.Handler)enumeration.nextElement();
      SnmpMibNode snmpMibNode = snmpRequestTree.getMetaNode(handler);
      int i = snmpRequestTree.getOidDepth(handler);
      Enumeration enumeration1 = snmpRequestTree.getSubRequests(handler);
      while (enumeration1.hasMoreElements())
        snmpMibNode.check((SnmpMibSubRequest)enumeration1.nextElement(), i); 
    } 
    if (paramSnmpMibRequest instanceof SnmpMibRequestImpl)
      ((SnmpMibRequestImpl)paramSnmpMibRequest).setRequestTree(snmpRequestTree); 
  }
  
  public void getNext(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException {
    SnmpRequestTree snmpRequestTree = getGetNextHandlers(paramSnmpMibRequest);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getNext", "Processing handlers for GET-NEXT... "); 
    Enumeration enumeration = snmpRequestTree.getHandlers();
    while (enumeration.hasMoreElements()) {
      SnmpRequestTree.Handler handler = (SnmpRequestTree.Handler)enumeration.nextElement();
      SnmpMibNode snmpMibNode = snmpRequestTree.getMetaNode(handler);
      int i = snmpRequestTree.getOidDepth(handler);
      Enumeration enumeration1 = snmpRequestTree.getSubRequests(handler);
      while (enumeration1.hasMoreElements())
        snmpMibNode.get((SnmpMibSubRequest)enumeration1.nextElement(), i); 
    } 
  }
  
  public void getBulk(SnmpMibRequest paramSnmpMibRequest, int paramInt1, int paramInt2) throws SnmpStatusException { getBulkWithGetNext(paramSnmpMibRequest, paramInt1, paramInt2); }
  
  public long[] getRootOid() {
    if (this.rootOid == null) {
      Vector vector = new Vector(10);
      this.root.getRootOid(vector);
      this.rootOid = new long[vector.size()];
      byte b = 0;
      Enumeration enumeration = vector.elements();
      while (enumeration.hasMoreElements()) {
        Integer integer = (Integer)enumeration.nextElement();
        this.rootOid[b++] = integer.longValue();
      } 
    } 
    return (long[])this.rootOid.clone();
  }
  
  private SnmpRequestTree getHandlers(SnmpMibRequest paramSnmpMibRequest, boolean paramBoolean1, boolean paramBoolean2, int paramInt) throws SnmpStatusException {
    SnmpRequestTree snmpRequestTree = new SnmpRequestTree(paramSnmpMibRequest, paramBoolean1, paramInt);
    byte b = 0;
    int i = paramSnmpMibRequest.getVersion();
    Enumeration enumeration = paramSnmpMibRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      try {
        this.root.findHandlingNode(snmpVarBind, snmpVarBind.oid.longValue(false), 0, snmpRequestTree);
      } catch (SnmpStatusException snmpStatusException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "Couldn't find a handling node for " + snmpVarBind.oid.toString()); 
        if (i == 0) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tV1: Throwing exception"); 
          SnmpStatusException snmpStatusException1 = new SnmpStatusException(snmpStatusException, b + true);
          snmpStatusException1.initCause(snmpStatusException);
          throw snmpStatusException1;
        } 
        if (paramInt == 253 || paramInt == 163) {
          int k = SnmpRequestTree.mapSetException(snmpStatusException.getStatus(), i);
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tSET: Throwing exception"); 
          SnmpStatusException snmpStatusException1 = new SnmpStatusException(k, b + true);
          snmpStatusException1.initCause(snmpStatusException);
          throw snmpStatusException1;
        } 
        if (paramBoolean2) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tATOMIC: Throwing exception"); 
          SnmpStatusException snmpStatusException1 = new SnmpStatusException(snmpStatusException, b + true);
          snmpStatusException1.initCause(snmpStatusException);
          throw snmpStatusException1;
        } 
        int j = SnmpRequestTree.mapGetException(snmpStatusException.getStatus(), i);
        if (j == 224) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tGET: Registering noSuchInstance"); 
          snmpVarBind.value = SnmpVarBind.noSuchInstance;
        } else if (j == 225) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tGET: Registering noSuchObject"); 
          snmpVarBind.value = SnmpVarBind.noSuchObject;
        } else {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tGET: Registering global error: " + j); 
          SnmpStatusException snmpStatusException1 = new SnmpStatusException(j, b + true);
          snmpStatusException1.initCause(snmpStatusException);
          throw snmpStatusException1;
        } 
      } 
      b++;
    } 
    return snmpRequestTree;
  }
  
  private SnmpRequestTree getGetNextHandlers(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException {
    SnmpRequestTree snmpRequestTree = new SnmpRequestTree(paramSnmpMibRequest, false, 161);
    snmpRequestTree.setGetNextFlag();
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", "Received MIB request : " + paramSnmpMibRequest); 
    AcmChecker acmChecker = new AcmChecker(paramSnmpMibRequest);
    byte b = 0;
    SnmpVarBind snmpVarBind = null;
    int i = paramSnmpMibRequest.getVersion();
    Object object = null;
    Enumeration enumeration = paramSnmpMibRequest.getElements();
    while (enumeration.hasMoreElements()) {
      snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      try {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", " Next OID of : " + snmpVarBind.oid); 
        SnmpOid snmpOid = new SnmpOid(this.root.findNextHandlingNode(snmpVarBind, snmpVarBind.oid.longValue(false), 0, 0, snmpRequestTree, acmChecker));
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", " is : " + snmpOid); 
        snmpVarBind.oid = snmpOid;
      } catch (SnmpStatusException snmpStatusException) {
        if (i == 0) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", "\tThrowing exception " + snmpStatusException.toString()); 
          throw new SnmpStatusException(snmpStatusException, b + true);
        } 
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", "Exception : " + snmpStatusException.getStatus()); 
        snmpVarBind.setSnmpValue(SnmpVarBind.endOfMibView);
      } 
      b++;
    } 
    return snmpRequestTree;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpMib.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */