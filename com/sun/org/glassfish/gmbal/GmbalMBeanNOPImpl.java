package com.sun.org.glassfish.gmbal;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ReflectionException;

public class GmbalMBeanNOPImpl implements GmbalMBean {
  public Object getAttribute(String paramString) throws AttributeNotFoundException, MBeanException, ReflectionException { return null; }
  
  public void setAttribute(Attribute paramAttribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {}
  
  public AttributeList getAttributes(String[] paramArrayOfString) { return null; }
  
  public AttributeList setAttributes(AttributeList paramAttributeList) { return null; }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws MBeanException, ReflectionException { return null; }
  
  public MBeanInfo getMBeanInfo() { return null; }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws ListenerNotFoundException {}
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws ListenerNotFoundException {}
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {}
  
  public MBeanNotificationInfo[] getNotificationInfo() { return new MBeanNotificationInfo[0]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\gmbal\GmbalMBeanNOPImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */