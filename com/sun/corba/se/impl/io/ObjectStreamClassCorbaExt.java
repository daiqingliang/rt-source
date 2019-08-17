package com.sun.corba.se.impl.io;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

class ObjectStreamClassCorbaExt {
  static final boolean isAbstractInterface(Class paramClass) {
    if (!paramClass.isInterface() || java.rmi.Remote.class.isAssignableFrom(paramClass))
      return false; 
    Method[] arrayOfMethod = paramClass.getMethods();
    for (byte b = 0; b < arrayOfMethod.length; b++) {
      Class[] arrayOfClass = arrayOfMethod[b].getExceptionTypes();
      boolean bool = false;
      for (byte b1 = 0; b1 < arrayOfClass.length && !bool; b1++) {
        if (java.rmi.RemoteException.class == arrayOfClass[b1] || Throwable.class == arrayOfClass[b1] || Exception.class == arrayOfClass[b1] || java.io.IOException.class == arrayOfClass[b1])
          bool = true; 
      } 
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  static final boolean isAny(String paramString) {
    boolean bool = false;
    if (paramString != null && (paramString.equals("Ljava/lang/Object;") || paramString.equals("Ljava/io/Serializable;") || paramString.equals("Ljava/io/Externalizable;")))
      bool = true; 
    return (bool == true);
  }
  
  private static final Method[] getDeclaredMethods(final Class clz) { return (Method[])AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return clz.getDeclaredMethods(); }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\ObjectStreamClassCorbaExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */