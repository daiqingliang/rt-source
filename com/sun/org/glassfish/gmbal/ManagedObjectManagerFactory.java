package com.sun.org.glassfish.gmbal;

import com.sun.org.glassfish.gmbal.util.GenericConstructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.ObjectName;

public final class ManagedObjectManagerFactory {
  private static GenericConstructor<ManagedObjectManager> objectNameCons = new GenericConstructor(ManagedObjectManager.class, "com.sun.org.glassfish.gmbal.impl.ManagedObjectManagerImpl", new Class[] { ObjectName.class });
  
  private static GenericConstructor<ManagedObjectManager> stringCons = new GenericConstructor(ManagedObjectManager.class, "com.sun.org.glassfish.gmbal.impl.ManagedObjectManagerImpl", new Class[] { String.class });
  
  public static Method getMethod(final Class<?> cls, final String name, Class<?>... types) {
    try {
      return (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception { return cls.getDeclaredMethod(name, types); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new GmbalException("Unexpected exception", privilegedActionException);
    } catch (SecurityException securityException) {
      throw new GmbalException("Unexpected exception", securityException);
    } 
  }
  
  public static ManagedObjectManager createStandalone(String paramString) {
    ManagedObjectManager managedObjectManager = (ManagedObjectManager)stringCons.create(new Object[] { paramString });
    return (managedObjectManager == null) ? ManagedObjectManagerNOPImpl.self : managedObjectManager;
  }
  
  public static ManagedObjectManager createFederated(ObjectName paramObjectName) {
    ManagedObjectManager managedObjectManager = (ManagedObjectManager)objectNameCons.create(new Object[] { paramObjectName });
    return (managedObjectManager == null) ? ManagedObjectManagerNOPImpl.self : managedObjectManager;
  }
  
  public static ManagedObjectManager createNOOP() { return ManagedObjectManagerNOPImpl.self; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\gmbal\ManagedObjectManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */