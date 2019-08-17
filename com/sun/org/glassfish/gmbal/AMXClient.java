package com.sun.org.glassfish.gmbal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.ModelMBeanInfo;

public class AMXClient implements AMXMBeanInterface {
  public static final ObjectName NULL_OBJECTNAME = makeObjectName("null:type=Null,name=Null");
  
  private MBeanServerConnection server;
  
  private ObjectName oname;
  
  private static ObjectName makeObjectName(String paramString) {
    try {
      return new ObjectName(paramString);
    } catch (MalformedObjectNameException malformedObjectNameException) {
      return null;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof AMXClient))
      return false; 
    AMXClient aMXClient = (AMXClient)paramObject;
    return this.oname.equals(aMXClient.oname);
  }
  
  public int hashCode() {
    null = 5;
    return 47 * null + ((this.oname != null) ? this.oname.hashCode() : 0);
  }
  
  public String toString() { return "AMXClient[" + this.oname + "]"; }
  
  private <T> T fetchAttribute(String paramString, Class<T> paramClass) {
    try {
      Object object = this.server.getAttribute(this.oname, paramString);
      return NULL_OBJECTNAME.equals(object) ? null : (T)paramClass.cast(object);
    } catch (JMException jMException) {
      throw new GmbalException("Exception in fetchAttribute", jMException);
    } catch (IOException iOException) {
      throw new GmbalException("Exception in fetchAttribute", iOException);
    } 
  }
  
  public AMXClient(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName) {
    this.server = paramMBeanServerConnection;
    this.oname = paramObjectName;
  }
  
  private AMXClient makeAMX(ObjectName paramObjectName) { return (paramObjectName == null) ? null : new AMXClient(this.server, paramObjectName); }
  
  public String getName() { return (String)fetchAttribute("Name", String.class); }
  
  public Map<String, ?> getMeta() {
    try {
      ModelMBeanInfo modelMBeanInfo = (ModelMBeanInfo)this.server.getMBeanInfo(this.oname);
      Descriptor descriptor = modelMBeanInfo.getMBeanDescriptor();
      HashMap hashMap = new HashMap();
      for (String str : descriptor.getFieldNames())
        hashMap.put(str, descriptor.getFieldValue(str)); 
      return hashMap;
    } catch (MBeanException mBeanException) {
      throw new GmbalException("Exception in getMeta", mBeanException);
    } catch (RuntimeOperationsException runtimeOperationsException) {
      throw new GmbalException("Exception in getMeta", runtimeOperationsException);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new GmbalException("Exception in getMeta", instanceNotFoundException);
    } catch (IntrospectionException introspectionException) {
      throw new GmbalException("Exception in getMeta", introspectionException);
    } catch (ReflectionException reflectionException) {
      throw new GmbalException("Exception in getMeta", reflectionException);
    } catch (IOException iOException) {
      throw new GmbalException("Exception in getMeta", iOException);
    } 
  }
  
  public AMXClient getParent() {
    ObjectName objectName = (ObjectName)fetchAttribute("Parent", ObjectName.class);
    return makeAMX(objectName);
  }
  
  public AMXClient[] getChildren() {
    ObjectName[] arrayOfObjectName = (ObjectName[])fetchAttribute("Children", ObjectName[].class);
    return makeAMXArray(arrayOfObjectName);
  }
  
  private AMXClient[] makeAMXArray(ObjectName[] paramArrayOfObjectName) {
    AMXClient[] arrayOfAMXClient = new AMXClient[paramArrayOfObjectName.length];
    byte b = 0;
    for (ObjectName objectName : paramArrayOfObjectName)
      arrayOfAMXClient[b++] = makeAMX(objectName); 
    return arrayOfAMXClient;
  }
  
  public Object getAttribute(String paramString) {
    try {
      return this.server.getAttribute(this.oname, paramString);
    } catch (MBeanException mBeanException) {
      throw new GmbalException("Exception in getAttribute", mBeanException);
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw new GmbalException("Exception in getAttribute", attributeNotFoundException);
    } catch (ReflectionException reflectionException) {
      throw new GmbalException("Exception in getAttribute", reflectionException);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new GmbalException("Exception in getAttribute", instanceNotFoundException);
    } catch (IOException iOException) {
      throw new GmbalException("Exception in getAttribute", iOException);
    } 
  }
  
  public void setAttribute(String paramString, Object paramObject) {
    Attribute attribute = new Attribute(paramString, paramObject);
    setAttribute(attribute);
  }
  
  public void setAttribute(Attribute paramAttribute) {
    try {
      this.server.setAttribute(this.oname, paramAttribute);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new GmbalException("Exception in setAttribute", instanceNotFoundException);
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw new GmbalException("Exception in setAttribute", attributeNotFoundException);
    } catch (InvalidAttributeValueException invalidAttributeValueException) {
      throw new GmbalException("Exception in setAttribute", invalidAttributeValueException);
    } catch (MBeanException mBeanException) {
      throw new GmbalException("Exception in setAttribute", mBeanException);
    } catch (ReflectionException reflectionException) {
      throw new GmbalException("Exception in setAttribute", reflectionException);
    } catch (IOException iOException) {
      throw new GmbalException("Exception in setAttribute", iOException);
    } 
  }
  
  public AttributeList getAttributes(String[] paramArrayOfString) {
    try {
      return this.server.getAttributes(this.oname, paramArrayOfString);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new GmbalException("Exception in getAttributes", instanceNotFoundException);
    } catch (ReflectionException reflectionException) {
      throw new GmbalException("Exception in getAttributes", reflectionException);
    } catch (IOException iOException) {
      throw new GmbalException("Exception in getAttributes", iOException);
    } 
  }
  
  public AttributeList setAttributes(AttributeList paramAttributeList) {
    try {
      return this.server.setAttributes(this.oname, paramAttributeList);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new GmbalException("Exception in setAttributes", instanceNotFoundException);
    } catch (ReflectionException reflectionException) {
      throw new GmbalException("Exception in setAttributes", reflectionException);
    } catch (IOException iOException) {
      throw new GmbalException("Exception in setAttributes", iOException);
    } 
  }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws MBeanException, ReflectionException {
    try {
      return this.server.invoke(this.oname, paramString, paramArrayOfObject, paramArrayOfString);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new GmbalException("Exception in invoke", instanceNotFoundException);
    } catch (IOException iOException) {
      throw new GmbalException("Exception in invoke", iOException);
    } 
  }
  
  public MBeanInfo getMBeanInfo() {
    try {
      return this.server.getMBeanInfo(this.oname);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new GmbalException("Exception in invoke", instanceNotFoundException);
    } catch (IntrospectionException introspectionException) {
      throw new GmbalException("Exception in invoke", introspectionException);
    } catch (ReflectionException reflectionException) {
      throw new GmbalException("Exception in invoke", reflectionException);
    } catch (IOException iOException) {
      throw new GmbalException("Exception in invoke", iOException);
    } 
  }
  
  public ObjectName objectName() { return this.oname; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\gmbal\AMXClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */