package com.sun.corba.se.spi.orbutil.proxy;

import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class DelegateInvocationHandlerImpl {
  public static InvocationHandler create(final Object delegate) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new DynamicAccessPermission("access")); 
    return new InvocationHandler() {
        public Object invoke(Object param1Object, Method param1Method, Object[] param1ArrayOfObject) throws Throwable {
          try {
            return param1Method.invoke(delegate, param1ArrayOfObject);
          } catch (InvocationTargetException invocationTargetException) {
            throw invocationTargetException.getCause();
          } 
        }
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\DelegateInvocationHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */