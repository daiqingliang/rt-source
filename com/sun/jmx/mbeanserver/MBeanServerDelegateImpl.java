package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.JMRuntimeException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

final class MBeanServerDelegateImpl extends MBeanServerDelegate implements DynamicMBean, MBeanRegistration {
  private static final String[] attributeNames = { "MBeanServerId", "SpecificationName", "SpecificationVersion", "SpecificationVendor", "ImplementationName", "ImplementationVersion", "ImplementationVendor" };
  
  private static final MBeanAttributeInfo[] attributeInfos = { new MBeanAttributeInfo("MBeanServerId", "java.lang.String", "The MBean server agent identification", true, false, false), new MBeanAttributeInfo("SpecificationName", "java.lang.String", "The full name of the JMX specification implemented by this product.", true, false, false), new MBeanAttributeInfo("SpecificationVersion", "java.lang.String", "The version of the JMX specification implemented by this product.", true, false, false), new MBeanAttributeInfo("SpecificationVendor", "java.lang.String", "The vendor of the JMX specification implemented by this product.", true, false, false), new MBeanAttributeInfo("ImplementationName", "java.lang.String", "The JMX implementation name (the name of this product)", true, false, false), new MBeanAttributeInfo("ImplementationVersion", "java.lang.String", "The JMX implementation version (the version of this product).", true, false, false), new MBeanAttributeInfo("ImplementationVendor", "java.lang.String", "the JMX implementation vendor (the vendor of this product).", true, false, false) };
  
  private final MBeanInfo delegateInfo = new MBeanInfo("javax.management.MBeanServerDelegate", "Represents  the MBean server from the management point of view.", attributeInfos, null, null, getNotificationInfo());
  
  public final ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception { return (paramObjectName == null) ? DELEGATE_NAME : paramObjectName; }
  
  public final void postRegister(Boolean paramBoolean) {}
  
  public final void preDeregister() { throw new IllegalArgumentException("The MBeanServerDelegate MBean cannot be unregistered"); }
  
  public final void postDeregister() {}
  
  public Object getAttribute(String paramString) throws AttributeNotFoundException, MBeanException, ReflectionException {
    try {
      if (paramString == null)
        throw new AttributeNotFoundException("null"); 
      if (paramString.equals("MBeanServerId"))
        return getMBeanServerId(); 
      if (paramString.equals("SpecificationName"))
        return getSpecificationName(); 
      if (paramString.equals("SpecificationVersion"))
        return getSpecificationVersion(); 
      if (paramString.equals("SpecificationVendor"))
        return getSpecificationVendor(); 
      if (paramString.equals("ImplementationName"))
        return getImplementationName(); 
      if (paramString.equals("ImplementationVersion"))
        return getImplementationVersion(); 
      if (paramString.equals("ImplementationVendor"))
        return getImplementationVendor(); 
      throw new AttributeNotFoundException("null");
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw attributeNotFoundException;
    } catch (JMRuntimeException jMRuntimeException) {
      throw jMRuntimeException;
    } catch (SecurityException securityException) {
      throw securityException;
    } catch (Exception exception) {
      throw new MBeanException(exception, "Failed to get " + paramString);
    } 
  }
  
  public void setAttribute(Attribute paramAttribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    String str = (paramAttribute == null) ? null : paramAttribute.getName();
    if (str == null) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Attribute name cannot be null");
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to invoke the setter on the MBean");
    } 
    Object object = getAttribute(str);
    throw new AttributeNotFoundException(str + " not accessible");
  }
  
  public AttributeList getAttributes(String[] paramArrayOfString) {
    String[] arrayOfString = (paramArrayOfString == null) ? attributeNames : paramArrayOfString;
    int i = arrayOfString.length;
    AttributeList attributeList = new AttributeList(i);
    for (byte b = 0; b < i; b++) {
      try {
        Attribute attribute = new Attribute(arrayOfString[b], getAttribute(arrayOfString[b]));
        attributeList.add(attribute);
      } catch (Exception exception) {
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanServerDelegateImpl.class.getName(), "getAttributes", "Attribute " + arrayOfString[b] + " not found"); 
      } 
    } 
    return attributeList;
  }
  
  public AttributeList setAttributes(AttributeList paramAttributeList) { return new AttributeList(0); }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws MBeanException, ReflectionException {
    if (paramString == null) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Operation name  cannot be null");
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to invoke the operation on the MBean");
    } 
    throw new ReflectionException(new NoSuchMethodException(paramString), "The operation with name " + paramString + " could not be found");
  }
  
  public MBeanInfo getMBeanInfo() { return this.delegateInfo; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MBeanServerDelegateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */