package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Enumeration;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

public class SnmpGenericObjectServer {
  protected final MBeanServer server;
  
  public SnmpGenericObjectServer(MBeanServer paramMBeanServer) { this.server = paramMBeanServer; }
  
  public void get(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException {
    int i = paramSnmpMibSubRequest.getSize();
    Object object = paramSnmpMibSubRequest.getUserData();
    String[] arrayOfString = new String[i];
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[i];
    long[] arrayOfLong = new long[i];
    byte b1 = 0;
    AttributeList attributeList = paramSnmpMibSubRequest.getElements();
    while (attributeList.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)attributeList.nextElement();
      try {
        long l = snmpVarBind.oid.getOidArc(paramInt);
        arrayOfString[b1] = paramSnmpGenericMetaServer.getAttributeName(l);
        arrayOfSnmpVarBind[b1] = snmpVarBind;
        arrayOfLong[b1] = l;
        paramSnmpGenericMetaServer.checkGetAccess(l, object);
        b1++;
      } catch (SnmpStatusException snmpStatusException) {
        paramSnmpMibSubRequest.registerGetException(snmpVarBind, snmpStatusException);
      } 
    } 
    attributeList = null;
    char c = 'à';
    try {
      AttributeList attributeList1 = this.server.getAttributes(paramObjectName, arrayOfString);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      AttributeList attributeList1 = new AttributeList();
    } catch (ReflectionException reflectionException) {
      AttributeList attributeList1 = new AttributeList();
    } catch (Exception exception) {
      attributeList = new AttributeList();
    } 
    Iterator iterator = attributeList.iterator();
    for (byte b2 = 0; b2 < b1; b2++) {
      if (!iterator.hasNext()) {
        SnmpStatusException snmpStatusException = new SnmpStatusException(c);
        paramSnmpMibSubRequest.registerGetException(arrayOfSnmpVarBind[b2], snmpStatusException);
      } else {
        Attribute attribute = (Attribute)iterator.next();
        while (b2 < b1 && !arrayOfString[b2].equals(attribute.getName())) {
          SnmpStatusException snmpStatusException = new SnmpStatusException(c);
          paramSnmpMibSubRequest.registerGetException(arrayOfSnmpVarBind[b2], snmpStatusException);
          b2++;
        } 
        if (b2 == b1)
          break; 
        try {
          (arrayOfSnmpVarBind[b2]).value = paramSnmpGenericMetaServer.buildSnmpValue(arrayOfLong[b2], attribute.getValue());
        } catch (SnmpStatusException snmpStatusException) {
          paramSnmpMibSubRequest.registerGetException(arrayOfSnmpVarBind[b2], snmpStatusException);
        } 
      } 
    } 
  }
  
  public SnmpValue get(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, long paramLong, Object paramObject) throws SnmpStatusException {
    String str = paramSnmpGenericMetaServer.getAttributeName(paramLong);
    Object object = null;
    try {
      object = this.server.getAttribute(paramObjectName, str);
    } catch (MBeanException mBeanException) {
      Exception exception = mBeanException.getTargetException();
      if (exception instanceof SnmpStatusException)
        throw (SnmpStatusException)exception; 
      throw new SnmpStatusException(224);
    } catch (Exception exception) {
      throw new SnmpStatusException(224);
    } 
    return paramSnmpGenericMetaServer.buildSnmpValue(paramLong, object);
  }
  
  public void set(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException {
    int i = paramSnmpMibSubRequest.getSize();
    AttributeList attributeList1 = new AttributeList(i);
    String[] arrayOfString = new String[i];
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[i];
    long[] arrayOfLong = new long[i];
    byte b1 = 0;
    AttributeList attributeList2 = paramSnmpMibSubRequest.getElements();
    while (attributeList2.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)attributeList2.nextElement();
      try {
        long l = snmpVarBind.oid.getOidArc(paramInt);
        String str = paramSnmpGenericMetaServer.getAttributeName(l);
        Object object = paramSnmpGenericMetaServer.buildAttributeValue(l, snmpVarBind.value);
        Attribute attribute = new Attribute(str, object);
        attributeList1.add(attribute);
        arrayOfString[b1] = str;
        arrayOfSnmpVarBind[b1] = snmpVarBind;
        arrayOfLong[b1] = l;
        b1++;
      } catch (SnmpStatusException snmpStatusException) {
        paramSnmpMibSubRequest.registerSetException(snmpVarBind, snmpStatusException);
      } 
    } 
    byte b2 = 6;
    try {
      AttributeList attributeList = this.server.setAttributes(paramObjectName, attributeList1);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      AttributeList attributeList = new AttributeList();
      b2 = 18;
    } catch (ReflectionException reflectionException) {
      b2 = 18;
      AttributeList attributeList = new AttributeList();
    } catch (Exception exception) {
      attributeList2 = new AttributeList();
    } 
    Iterator iterator = attributeList2.iterator();
    for (byte b3 = 0; b3 < b1; b3++) {
      if (!iterator.hasNext()) {
        SnmpStatusException snmpStatusException = new SnmpStatusException(b2);
        paramSnmpMibSubRequest.registerSetException(arrayOfSnmpVarBind[b3], snmpStatusException);
      } else {
        Attribute attribute = (Attribute)iterator.next();
        while (b3 < b1 && !arrayOfString[b3].equals(attribute.getName())) {
          SnmpStatusException snmpStatusException = new SnmpStatusException(6);
          paramSnmpMibSubRequest.registerSetException(arrayOfSnmpVarBind[b3], snmpStatusException);
          b3++;
        } 
        if (b3 == b1)
          break; 
        try {
          (arrayOfSnmpVarBind[b3]).value = paramSnmpGenericMetaServer.buildSnmpValue(arrayOfLong[b3], attribute.getValue());
        } catch (SnmpStatusException snmpStatusException) {
          paramSnmpMibSubRequest.registerSetException(arrayOfSnmpVarBind[b3], snmpStatusException);
        } 
      } 
    } 
  }
  
  public SnmpValue set(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    String str = paramSnmpGenericMetaServer.getAttributeName(paramLong);
    Object object1 = paramSnmpGenericMetaServer.buildAttributeValue(paramLong, paramSnmpValue);
    Attribute attribute = new Attribute(str, object1);
    Object object2 = null;
    try {
      this.server.setAttribute(paramObjectName, attribute);
      object2 = this.server.getAttribute(paramObjectName, str);
    } catch (InvalidAttributeValueException invalidAttributeValueException) {
      throw new SnmpStatusException(10);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new SnmpStatusException(18);
    } catch (ReflectionException reflectionException) {
      throw new SnmpStatusException(18);
    } catch (MBeanException mBeanException) {
      Exception exception = mBeanException.getTargetException();
      if (exception instanceof SnmpStatusException)
        throw (SnmpStatusException)exception; 
      throw new SnmpStatusException(6);
    } catch (Exception exception) {
      throw new SnmpStatusException(6);
    } 
    return paramSnmpGenericMetaServer.buildSnmpValue(paramLong, object2);
  }
  
  public void check(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException {
    Object object = paramSnmpMibSubRequest.getUserData();
    Enumeration enumeration = paramSnmpMibSubRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      try {
        long l = snmpVarBind.oid.getOidArc(paramInt);
        check(paramSnmpGenericMetaServer, paramObjectName, snmpVarBind.value, l, object);
      } catch (SnmpStatusException snmpStatusException) {
        paramSnmpMibSubRequest.registerCheckException(snmpVarBind, snmpStatusException);
      } 
    } 
  }
  
  public void check(SnmpGenericMetaServer paramSnmpGenericMetaServer, ObjectName paramObjectName, SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException {
    paramSnmpGenericMetaServer.checkSetAccess(paramSnmpValue, paramLong, paramObject);
    try {
      String str = paramSnmpGenericMetaServer.getAttributeName(paramLong);
      Object object = paramSnmpGenericMetaServer.buildAttributeValue(paramLong, paramSnmpValue);
      Object[] arrayOfObject = new Object[1];
      String[] arrayOfString = new String[1];
      arrayOfObject[0] = object;
      arrayOfString[0] = object.getClass().getName();
      this.server.invoke(paramObjectName, "check" + str, arrayOfObject, arrayOfString);
    } catch (SnmpStatusException snmpStatusException) {
      throw snmpStatusException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new SnmpStatusException(18);
    } catch (ReflectionException reflectionException) {
    
    } catch (MBeanException mBeanException) {
      Exception exception = mBeanException.getTargetException();
      if (exception instanceof SnmpStatusException)
        throw (SnmpStatusException)exception; 
      throw new SnmpStatusException(6);
    } catch (Exception exception) {
      throw new SnmpStatusException(6);
    } 
  }
  
  public void registerTableEntry(SnmpMibTable paramSnmpMibTable, SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject) throws SnmpStatusException {
    if (paramObjectName == null)
      throw new SnmpStatusException(18); 
    try {
      if (paramObject != null && !this.server.isRegistered(paramObjectName))
        this.server.registerMBean(paramObject, paramObjectName); 
    } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
      throw new SnmpStatusException(18);
    } catch (MBeanRegistrationException mBeanRegistrationException) {
      throw new SnmpStatusException(6);
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      throw new SnmpStatusException(5);
    } catch (RuntimeOperationsException runtimeOperationsException) {
      throw new SnmpStatusException(5);
    } catch (Exception exception) {
      throw new SnmpStatusException(5);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpGenericObjectServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */