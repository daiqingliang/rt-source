package com.sun.jmx.mbeanserver;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import sun.reflect.misc.ReflectUtil;

public abstract class MBeanSupport<M> extends Object implements DynamicMBean2, MBeanRegistration {
  private final MBeanInfo mbeanInfo;
  
  private final Object resource;
  
  private final PerInterface<M> perInterface;
  
  <T> MBeanSupport(T paramT, Class<T> paramClass) throws NotCompliantMBeanException {
    if (paramClass == null)
      throw new NotCompliantMBeanException("Null MBean interface"); 
    if (!paramClass.isInstance(paramT)) {
      String str = "Resource class " + paramT.getClass().getName() + " is not an instance of " + paramClass.getName();
      throw new NotCompliantMBeanException(str);
    } 
    ReflectUtil.checkPackageAccess(paramClass);
    this.resource = paramT;
    MBeanIntrospector mBeanIntrospector = getMBeanIntrospector();
    this.perInterface = mBeanIntrospector.getPerInterface(paramClass);
    this.mbeanInfo = mBeanIntrospector.getMBeanInfo(paramT, this.perInterface);
  }
  
  abstract MBeanIntrospector<M> getMBeanIntrospector();
  
  abstract Object getCookie();
  
  public final boolean isMXBean() { return this.perInterface.isMXBean(); }
  
  public abstract void register(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception;
  
  public abstract void unregister();
  
  public final ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    if (this.resource instanceof MBeanRegistration)
      paramObjectName = ((MBeanRegistration)this.resource).preRegister(paramMBeanServer, paramObjectName); 
    return paramObjectName;
  }
  
  public final void preRegister2(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception { register(paramMBeanServer, paramObjectName); }
  
  public final void registerFailed() { unregister(); }
  
  public final void postRegister(Boolean paramBoolean) {
    if (this.resource instanceof MBeanRegistration)
      ((MBeanRegistration)this.resource).postRegister(paramBoolean); 
  }
  
  public final void preDeregister() {
    if (this.resource instanceof MBeanRegistration)
      ((MBeanRegistration)this.resource).preDeregister(); 
  }
  
  public final void postDeregister() {
    try {
      unregister();
    } finally {
      if (this.resource instanceof MBeanRegistration)
        ((MBeanRegistration)this.resource).postDeregister(); 
    } 
  }
  
  public final Object getAttribute(String paramString) throws AttributeNotFoundException, MBeanException, ReflectionException { return this.perInterface.getAttribute(this.resource, paramString, getCookie()); }
  
  public final AttributeList getAttributes(String[] paramArrayOfString) {
    AttributeList attributeList = new AttributeList(paramArrayOfString.length);
    for (String str : paramArrayOfString) {
      try {
        Object object = getAttribute(str);
        attributeList.add(new Attribute(str, object));
      } catch (Exception exception) {}
    } 
    return attributeList;
  }
  
  public final void setAttribute(Attribute paramAttribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    String str = paramAttribute.getName();
    Object object = paramAttribute.getValue();
    this.perInterface.setAttribute(this.resource, str, object, getCookie());
  }
  
  public final AttributeList setAttributes(AttributeList paramAttributeList) {
    AttributeList attributeList = new AttributeList(paramAttributeList.size());
    for (Object object : paramAttributeList) {
      Attribute attribute = (Attribute)object;
      try {
        setAttribute(attribute);
        attributeList.add(new Attribute(attribute.getName(), attribute.getValue()));
      } catch (Exception exception) {}
    } 
    return attributeList;
  }
  
  public final Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws MBeanException, ReflectionException { return this.perInterface.invoke(this.resource, paramString, paramArrayOfObject, paramArrayOfString, getCookie()); }
  
  public MBeanInfo getMBeanInfo() { return this.mbeanInfo; }
  
  public final String getClassName() { return this.resource.getClass().getName(); }
  
  public final Object getResource() { return this.resource; }
  
  public final Class<?> getMBeanInterface() { return this.perInterface.getMBeanInterface(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MBeanSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */