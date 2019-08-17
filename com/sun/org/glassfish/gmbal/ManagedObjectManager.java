package com.sun.org.glassfish.gmbal;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public interface ManagedObjectManager extends Closeable {
  void suspendJMXRegistration();
  
  void resumeJMXRegistration();
  
  boolean isManagedObject(Object paramObject);
  
  GmbalMBean createRoot();
  
  GmbalMBean createRoot(Object paramObject);
  
  GmbalMBean createRoot(Object paramObject, String paramString);
  
  Object getRoot();
  
  GmbalMBean register(Object paramObject1, Object paramObject2, String paramString);
  
  GmbalMBean register(Object paramObject1, Object paramObject2);
  
  GmbalMBean registerAtRoot(Object paramObject, String paramString);
  
  GmbalMBean registerAtRoot(Object paramObject);
  
  void unregister(Object paramObject);
  
  ObjectName getObjectName(Object paramObject);
  
  AMXClient getAMXClient(Object paramObject);
  
  Object getObject(ObjectName paramObjectName);
  
  void stripPrefix(String... paramVarArgs);
  
  void stripPackagePrefix();
  
  String getDomain();
  
  void setMBeanServer(MBeanServer paramMBeanServer);
  
  MBeanServer getMBeanServer();
  
  void setResourceBundle(ResourceBundle paramResourceBundle);
  
  ResourceBundle getResourceBundle();
  
  void addAnnotation(AnnotatedElement paramAnnotatedElement, Annotation paramAnnotation);
  
  void setRegistrationDebug(RegistrationDebugLevel paramRegistrationDebugLevel);
  
  void setRuntimeDebug(boolean paramBoolean);
  
  void setTypelibDebug(int paramInt);
  
  void setJMXRegistrationDebug(boolean paramBoolean);
  
  String dumpSkeleton(Object paramObject);
  
  void suppressDuplicateRootReport(boolean paramBoolean);
  
  public enum RegistrationDebugLevel {
    NONE, NORMAL, FINE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\gmbal\ManagedObjectManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */