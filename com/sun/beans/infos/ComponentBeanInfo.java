package com.sun.beans.infos;

import java.awt.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class ComponentBeanInfo extends SimpleBeanInfo {
  private static final Class<Component> beanClass = Component.class;
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor propertyDescriptor1 = new PropertyDescriptor("name", beanClass);
      PropertyDescriptor propertyDescriptor2 = new PropertyDescriptor("background", beanClass);
      PropertyDescriptor propertyDescriptor3 = new PropertyDescriptor("foreground", beanClass);
      PropertyDescriptor propertyDescriptor4 = new PropertyDescriptor("font", beanClass);
      PropertyDescriptor propertyDescriptor5 = new PropertyDescriptor("enabled", beanClass);
      PropertyDescriptor propertyDescriptor6 = new PropertyDescriptor("visible", beanClass);
      PropertyDescriptor propertyDescriptor7 = new PropertyDescriptor("focusable", beanClass);
      propertyDescriptor5.setExpert(true);
      propertyDescriptor6.setHidden(true);
      propertyDescriptor2.setBound(true);
      propertyDescriptor3.setBound(true);
      propertyDescriptor4.setBound(true);
      propertyDescriptor7.setBound(true);
      return new PropertyDescriptor[] { propertyDescriptor1, propertyDescriptor2, propertyDescriptor3, propertyDescriptor4, propertyDescriptor5, propertyDescriptor6, propertyDescriptor7 };
    } catch (IntrospectionException introspectionException) {
      throw new Error(introspectionException.toString());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\infos\ComponentBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */