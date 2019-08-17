package com.sun.corba.se.spi.orbutil.proxy;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompositeInvocationHandlerImpl implements CompositeInvocationHandler {
  private Map classToInvocationHandler = new LinkedHashMap();
  
  private InvocationHandler defaultHandler = null;
  
  private static final DynamicAccessPermission perm = new DynamicAccessPermission("access");
  
  private static final long serialVersionUID = 4571178305984833743L;
  
  public void addInvocationHandler(Class paramClass, InvocationHandler paramInvocationHandler) {
    checkAccess();
    this.classToInvocationHandler.put(paramClass, paramInvocationHandler);
  }
  
  public void setDefaultHandler(InvocationHandler paramInvocationHandler) {
    checkAccess();
    this.defaultHandler = paramInvocationHandler;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    Class clazz = paramMethod.getDeclaringClass();
    InvocationHandler invocationHandler = (InvocationHandler)this.classToInvocationHandler.get(clazz);
    if (invocationHandler == null)
      if (this.defaultHandler != null) {
        invocationHandler = this.defaultHandler;
      } else {
        ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get("util");
        throw oRBUtilSystemException.noInvocationHandler("\"" + paramMethod.toString() + "\"");
      }  
    return invocationHandler.invoke(paramObject, paramMethod, paramArrayOfObject);
  }
  
  private void checkAccess() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(perm); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\CompositeInvocationHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */