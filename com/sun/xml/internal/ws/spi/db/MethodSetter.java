package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class MethodSetter extends PropertySetterBase {
  private Method method;
  
  public MethodSetter(Method paramMethod) {
    this.method = paramMethod;
    this.type = paramMethod.getParameterTypes()[0];
  }
  
  public Method getMethod() { return this.method; }
  
  public <A> A getAnnotation(Class<A> paramClass) {
    Class<A> clazz = paramClass;
    return (A)this.method.getAnnotation(clazz);
  }
  
  public void set(final Object instance, Object paramObject2) {
    final Object[] args = { paramObject2 };
    if (this.method.isAccessible()) {
      try {
        this.method.invoke(paramObject1, arrayOfObject);
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } else {
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
              public Object run() throws IllegalAccessException {
                if (!MethodSetter.this.method.isAccessible())
                  MethodSetter.this.method.setAccessible(true); 
                try {
                  MethodSetter.this.method.invoke(instance, args);
                } catch (Exception exception) {
                  exception.printStackTrace();
                } 
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        privilegedActionException.printStackTrace();
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\MethodSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */