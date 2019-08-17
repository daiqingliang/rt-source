package com.sun.beans.finder;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public final class BeanInfoFinder extends InstanceFinder<BeanInfo> {
  private static final String DEFAULT = "sun.beans.infos";
  
  private static final String DEFAULT_NEW = "com.sun.beans.infos";
  
  public BeanInfoFinder() { super(BeanInfo.class, true, "BeanInfo", new String[] { "sun.beans.infos" }); }
  
  private static boolean isValid(Class<?> paramClass, Method paramMethod) { return (paramMethod != null && paramMethod.getDeclaringClass().isAssignableFrom(paramClass)); }
  
  protected BeanInfo instantiate(Class<?> paramClass, String paramString1, String paramString2) {
    if ("sun.beans.infos".equals(paramString1))
      paramString1 = "com.sun.beans.infos"; 
    BeanInfo beanInfo = (!"com.sun.beans.infos".equals(paramString1) || "ComponentBeanInfo".equals(paramString2)) ? (BeanInfo)super.instantiate(paramClass, paramString1, paramString2) : null;
    if (beanInfo != null) {
      BeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();
      if (beanDescriptor != null) {
        if (paramClass.equals(beanDescriptor.getBeanClass()))
          return beanInfo; 
      } else {
        PropertyDescriptor[] arrayOfPropertyDescriptor = beanInfo.getPropertyDescriptors();
        if (arrayOfPropertyDescriptor != null) {
          for (PropertyDescriptor propertyDescriptor : arrayOfPropertyDescriptor) {
            Method method = propertyDescriptor.getReadMethod();
            if (method == null)
              method = propertyDescriptor.getWriteMethod(); 
            if (isValid(paramClass, method))
              return beanInfo; 
          } 
        } else {
          MethodDescriptor[] arrayOfMethodDescriptor = beanInfo.getMethodDescriptors();
          if (arrayOfMethodDescriptor != null)
            for (MethodDescriptor methodDescriptor : arrayOfMethodDescriptor) {
              if (isValid(paramClass, methodDescriptor.getMethod()))
                return beanInfo; 
            }  
        } 
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\BeanInfoFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */