package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class MethodGetter extends PropertyGetterBase {
  private Method method;
  
  public MethodGetter(Method paramMethod) {
    this.method = paramMethod;
    this.type = paramMethod.getReturnType();
  }
  
  public Method getMethod() { return this.method; }
  
  public <A> A getAnnotation(Class<A> paramClass) {
    Class<A> clazz = paramClass;
    return (A)this.method.getAnnotation(clazz);
  }
  
  public Object get(Object paramObject) {
    Object[] arrayOfObject = new Object[0];
    try {
      if (this.method.isAccessible())
        return this.method.invoke(paramObject, arrayOfObject); 
      PrivilegedGetter privilegedGetter = new PrivilegedGetter(this.method, paramObject);
      try {
        AccessController.doPrivileged(privilegedGetter);
      } catch (PrivilegedActionException privilegedActionException) {
        privilegedActionException.printStackTrace();
      } 
      return privilegedGetter.value;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  static class PrivilegedGetter implements PrivilegedExceptionAction {
    private Object value;
    
    private Method method;
    
    private Object instance;
    
    public PrivilegedGetter(Method param1Method, Object param1Object) {
      this.method = param1Method;
      this.instance = param1Object;
    }
    
    public Object run() throws IllegalAccessException {
      if (!this.method.isAccessible())
        this.method.setAccessible(true); 
      try {
        this.value = this.method.invoke(this.instance, new Object[0]);
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\MethodGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */